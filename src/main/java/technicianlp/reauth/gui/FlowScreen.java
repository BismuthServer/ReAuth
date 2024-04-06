package technicianlp.reauth.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import org.lwjgl.opengl.GL11;
import technicianlp.reauth.ReAuth;
import technicianlp.reauth.authentication.SessionData;
import technicianlp.reauth.authentication.flows.*;
import technicianlp.reauth.configuration.Config;
import technicianlp.reauth.configuration.Profile;
import technicianlp.reauth.session.SessionHelper;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;

public class FlowScreen extends AbstractScreen implements FlowCallback {

    public static <F extends Flow, P> F open(BiFunction<P, FlowCallback, F> flowConstructor, P param, Screen background) {
        FlowScreen screen = new FlowScreen(background);
        F flow = flowConstructor.apply(param, screen);
        screen.flow = flow;
        Minecraft.getInstance().openScreen(screen);
        return flow;
    }

    private Flow flow;
    private FlowStage stage = FlowStage.INITIAL;
    private String[] formatArgs = new String[0];
    private String errorText = null;

    public FlowScreen(Screen background) {
        super("reauth.gui.title.flow", background);
    }

    @Override
    public final void init() {
        super.init();

        int buttonWidth = 196;
        int buttonWidthH = buttonWidth / 2;

        this.formatArgs = new String[0];
        this.errorText = null;
        if (this.stage == FlowStage.MS_AWAIT_AUTH_CODE && this.flow instanceof AuthorizationCodeFlow) {
            this.addButton(new ButtonWidget(2, this.centerX - buttonWidthH, this.baseY + this.screenHeight - 42, buttonWidth, 20, I18n.translate("reauth.msauth.button.browser")));
        } else if (this.stage == FlowStage.MS_POLL_DEVICE_CODE && this.flow instanceof DeviceCodeFlow) {
            DeviceCodeFlow flow = (DeviceCodeFlow) this.flow;
            if (CompletableFuture.allOf(flow.getLoginUrl(), flow.getCode()).isDone()) {
                this.addButton(new ButtonWidget(3, this.centerX - buttonWidthH, this.baseY + this.screenHeight - 42, buttonWidth, 20, I18n.translate("reauth.msauth.button.browser")));
                this.formatArgs = new String[]{flow.getLoginUrl().join(), flow.getCode().join()};
            }
        } else if (this.stage == FlowStage.FAILED) {
            this.errorText = Flows.getFailureReason(this.flow);
        }
    }

    @Override
    public final void render(int mouseX, int mouseY, float partialTicks) {
        super.render(mouseX, mouseY, partialTicks);

        String text = I18n.translate(this.stage.getRawName(), (Object[]) this.formatArgs);
        List<String> lines = new ArrayList<>();

        Collections.addAll(lines, text.split("\\\\n"));
        if(this.errorText != null) {
            lines.add("");
            Collections.addAll(lines, I18n.translate(this.errorText).split("\\\\n"));
        }

        int height = lines.size() * 9;
        for (String s : lines) {
            if (s.startsWith("$")) {
                height += 9;
            }
        }

        int y = this.centerY - height / 2;
        for (String line : lines) {
            if (line.startsWith("$")) {
                line = line.substring(1);
                GL11.glPushMatrix();
                GL11.glScalef(2, 2, 1);
                this.textRenderer.drawWithShadow(line, (float) (this.centerX - this.textRenderer.getWidth(line)) / 2, (float) y / 2, 0xFFFFFFFF);
                y += 18;
                GL11.glPopMatrix();
            } else {
                this.textRenderer.drawWithShadow(line, (float) (this.centerX - this.textRenderer.getWidth(line) / 2), (float) y, 0xFFFFFFFF);
                y += 9;
            }
        }
    }

    @Override
    public final void removed() {
        super.removed();
        if (this.stage != FlowStage.FINISHED) {
            this.flow.cancel();
        }
    }

    @Override
    protected void buttonClicked(ButtonWidget button) {
        super.buttonClicked(button);

        String uriString = null;
        if (button.id == 2 && this.flow instanceof AuthorizationCodeFlow) {
            uriString = ((AuthorizationCodeFlow) this.flow).getLoginUrl();
        } else if (button.id == 3 && this.flow instanceof DeviceCodeFlow) {
            CompletableFuture<String> loginUrl = ((DeviceCodeFlow) this.flow).getLoginUrl();
            if (loginUrl.isDone()) {
                uriString = loginUrl.join();
            }
        }
        if (uriString != null) {
            try {
                URI uri = new URI(uriString);
                ((IGuiScreen) this).reAuth$doOpenWebLink(uri);
            } catch (URISyntaxException e) {
                ReAuth.getLog().error("Browser button failed", e);
            }
        }
    }

    @Override
    public void onSessionComplete(SessionData session, Throwable throwable) {
        if (throwable == null) {
            SessionHelper.setSession(session);
            ReAuth.getLog().info("Login complete");
        } else {
            if (throwable instanceof CancellationException || throwable.getCause() instanceof CancellationException) {
                ReAuth.getLog().info("Login cancelled");
            } else {
                ReAuth.getLog().error("Login failed", throwable);
            }
        }
    }

    @Override
    public void onProfileComplete(Profile profile, Throwable throwable) {
        if (throwable == null) {
			Config.getInstance().storeProfile(profile);
            ReAuth.getLog().info("Profile saved successfully");
        } else {
            if (throwable instanceof CancellationException || throwable.getCause() instanceof CancellationException) {
                ReAuth.getLog().info("Profile saving cancelled");
            } else {
                ReAuth.getLog().error("Profile failed to save", throwable);
            }
        }
    }

    @Override
    public final void transitionStage(FlowStage newStage) {
        this.stage = newStage;
        ReAuth.getLog().info(this.stage.getLogLine());
        this.init(Minecraft.getInstance(), this.width, this.height);

        if (newStage == FlowStage.MS_AWAIT_AUTH_CODE && this.flow instanceof AuthorizationCodeFlow) {
            try {
                URI uri = new URI(((AuthorizationCodeFlow) this.flow).getLoginUrl());
                ((IGuiScreen) this).reAuth$doOpenWebLink(uri);
            } catch (URISyntaxException e) {
                ReAuth.getLog().error("Failed to open page", e);
            }
        } else if (newStage == FlowStage.FINISHED) {
            this.requestClose(true);
        }
    }

    @Override
    public final Executor getExecutor() {
        return ReAuth.executor;
    }
}
