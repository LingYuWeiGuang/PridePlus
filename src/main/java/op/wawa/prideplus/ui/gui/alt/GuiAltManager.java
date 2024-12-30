package op.wawa.prideplus.ui.gui.alt;

import op.wawa.prideplus.ui.font.FontManager;
import op.wawa.prideplus.ui.gui.alt.altimpl.MicrosoftAlt;
import op.wawa.prideplus.ui.gui.alt.altimpl.MojangAlt;
import op.wawa.prideplus.ui.gui.alt.altimpl.OfflineAlt;
import op.wawa.prideplus.ui.gui.alt.altimpl.OriginalAlt;
import op.wawa.prideplus.utils.object.SlidingCalculation;
import op.wawa.prideplus.utils.render.ColorUtils;
import op.wawa.prideplus.utils.render.RenderUtils;
import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import op.wawa.prideplus.ui.gui.alt.microsoft.GuiLoginMicrosoftAccount;
import op.wawa.prideplus.ui.gui.alt.microsoft.MicrosoftAuth;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.Session;
import net.minecraft.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.net.Proxy;

public final class GuiAltManager extends GuiScreen {
    private final GuiScreen parentScreen;
    private static final SlidingCalculation slidingCalculation = new SlidingCalculation(30,30);

    private GuiButton buttonLogin;
    private GuiButton buttonRemove;
    private volatile String status = ChatFormatting.YELLOW + "等待中...";

    private static Alt selectAlt;

    public GuiAltManager(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        buttonLogin.enabled = buttonRemove.enabled = selectAlt != null;

        RenderUtils.drawRect(0, 0, 200, height, ColorUtils.getRGB(0,0,0,80));

        FontManager.default16.drawCenteredString2(ChatFormatting.YELLOW + "当前用户名:" + mc.getSession().getUsername(),width / 2.0f,height / 2.0f - 10,-1);
        FontManager.default16.drawCenteredString2(status,width / 2.0f,height / 2.0f,-1);

        double altY = 2 - slidingCalculation.getCurrent();
        for (Alt alt : AltManager.INSTANCE.getAltList()) {
            RenderUtils.drawRect(2F, (float) altY, 198F, (float) (altY + 50), ColorUtils.getRGB(50,50,50,150));

            if (alt == selectAlt) {
                RenderUtils.drawBorderedRect(2F, (float) altY, 198F, (float) (altY + 50),1,-1,0);
            } else if (RenderUtils.isHovered(2F, (float) altY, 198F, (float) (altY + 50),mouseX,mouseY) && Mouse.isButtonDown(0)) {
                selectAlt = alt;
            }

            GlStateManager.pushMatrix();
            GlStateManager.scale(1.5,1.5,1.5);
            FontManager.default16.drawCenteredString2(alt.getUserName(),98 / 1.5f,((float) altY + 4) / 1.5f,-1);
            GlStateManager.popMatrix();

            switch (alt.getAccountType()) {
                case OFFLINE:
                    FontManager.default16.drawCenteredString2("离线账户",98,((float) altY + 22),ColorUtils.getRGB(255,100,100));
                    break;
                case MOJANG:
                    FontManager.default16.drawCenteredString2("账号:" + ((MojangAlt) alt).getAccount(),98,((float) altY + 22),ColorUtils.getRGB(150,150,150));
                    break;
                case MICROSOFT:
                    FontManager.default16.drawCenteredString2("微软账户",98,((float) altY + 22),ColorUtils.getRGB(0,255,0));
                    break;
                case ORIGINAL: {
                    final OriginalAlt originalAlt = (OriginalAlt) alt;
                    FontManager.default16.drawCenteredString2("原始账户", 98, ((float) altY + 22), ColorUtils.getRGB(255, 255, 0));
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(0.5, 0.5, 0.5);
                    FontManager.default16.drawCenteredString2("Type:" + originalAlt.getType(), 98 * 2f, ((float) altY + 36) * 2f, ColorUtils.getRGB(150,150,150));
                    FontManager.default16.drawCenteredString2("UUID:" + originalAlt.getUUID(), 98 * 2f, ((float) altY + 41) * 2f, ColorUtils.getRGB(150,150,150));
                    GlStateManager.popMatrix();
                    break;
                }
            }

            slidingCalculation.calculation();

            if (slidingCalculation.getCurrent() < 0) {
                slidingCalculation.setCurrent(0);
            }

            altY += 55;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            mc.displayGuiScreen(parentScreen);
        } else if (button.id == 1) {
            mc.displayGuiScreen(new GuiAltLogin(this) {
                @Override
                public void onLogin(String account, String password) {
                    if (StringUtils.isNullOrEmpty(password)) {
                        status = ChatFormatting.GREEN + "增添成功! " + account;
                        AltManager.INSTANCE.getAltList().add(new OfflineAlt(account));
                    } else {
                        final YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
                        final YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) service.createUserAuthentication(Agent.MINECRAFT);
                        auth.setUsername(account);
                        auth.setPassword(password);

                        status = ChatFormatting.YELLOW + "增添中...";

                        new Thread() {
                            @Override
                            public void run() {
                                try {
                                    auth.logIn();
                                    status = ChatFormatting.GREEN + "增添成功! " + account;

                                    AltManager.INSTANCE.getAltList().add(new MojangAlt(account,password,auth.getSelectedProfile().getName()));
                                } catch (AuthenticationException e) {
                                    e.printStackTrace();
                                    status = ChatFormatting.RED + "增加失败! " + e.getClass().getName() + ": " + e.getMessage();
                                }

                                interrupt();
                            }
                        }.start();
                    }
                }
            });
        } else if (button.id == 2) {
            if (selectAlt != null) {
                new Thread() {
                    @Override
                    public void run() {
                        status = ChatFormatting.YELLOW + "登录中...";

                        switch (selectAlt.getAccountType()) {
                            case OFFLINE:
                                Minecraft.getMinecraft().setSession(new Session(selectAlt.getUserName(), "", "", "mojang"));
                                status = ChatFormatting.GREEN + "登录成功! " + mc.getSession().getUsername();
                                break;
                            case MOJANG: {
                                try {
                                    final MojangAlt mojangAlt = (MojangAlt) selectAlt;
                                    final AltManager.LoginStatus loginStatus = AltManager.loginAlt(mojangAlt.getAccount(), mojangAlt.getPassword());

                                    switch (loginStatus) {
                                        case FAILED:
                                            status = ChatFormatting.RED + "登录失败!";
                                            break;
                                        case SUCCESS:
                                            status = ChatFormatting.GREEN + "登录成功! " + mc.getSession().getUsername();
                                            break;
                                    }
                                } catch (AuthenticationException e) {
                                    if (e.getMessage().equals("Migrated")) {
                                        status = ChatFormatting.RED + "此用户迁移至微软了!";
                                    } else {
                                        e.printStackTrace();
                                        status = ChatFormatting.RED + "登录失败! " + e.getClass().getName() + ": " + e.getMessage();
                                    }
                                }
                                break;
                            }
                            case MICROSOFT: {
                                new Thread("AltManager login microsoft thread") {
                                    @Override
                                    public void run() {
                                        new MicrosoftAuth(
                                                data -> mc.setSession(new Session(data.getUserName(),data.getUuid(),data.getAccessToken(),"mojang")),
                                                status -> GuiAltManager.this.status = status,
                                                ((MicrosoftAlt) selectAlt).getRefreshToken()
                                        );
                                    }
                                }.start();

                                break;
                            }
                            case ORIGINAL: {
                                final OriginalAlt originalAlt = (OriginalAlt) selectAlt;
                                mc.setSession(new Session(originalAlt.getUserName(),originalAlt.getUUID(),originalAlt.getAccessToken(),originalAlt.getType()));
                                status = ChatFormatting.GREEN + "登录成功! " + mc.getSession().getUsername();
                                break;
                            }
                        }

                        interrupt();
                    }
                }.start();
            }
        } else if (button.id == 3) {
            if (selectAlt != null) {
                AltManager.INSTANCE.getAltList().remove(selectAlt);
                selectAlt = null;
            }
        } else if (button.id == 4) {
            mc.displayGuiScreen(new GuiAltLogin(this) {
                @Override
                public void onLogin(String account,String password) {
                    new Thread() {
                        @Override
                        public void run() {
                            final AltManager.LoginStatus loginStatus;
                            try {
                                status = ChatFormatting.YELLOW + "登录中...";
                                loginStatus = AltManager.loginAlt(account, password);

                                switch (loginStatus) {
                                    case FAILED:
                                        status = ChatFormatting.RED + "登录失败!";
                                        break;
                                    case SUCCESS:
                                        status = ChatFormatting.GREEN + "登录成功! " + mc.getSession().getUsername();
                                        break;
                                }
                            } catch (AuthenticationException e) {
                                e.printStackTrace();
                                status = ChatFormatting.RED + "登录失败! " + e.getClass().getName() + ": " + e.getMessage();
                            }

                            interrupt();
                        }
                    }.start();
                }
            });
        } else if (button.id == 5) {
            mc.displayGuiScreen(new GuiLoginMicrosoftAccount(this) {
                @Override
                protected void onLogin(@NotNull MicrosoftAuth.Data data) {
                    mc.setSession(new Session(data.getUserName(), data.getUuid(), data.getAccessToken(), "mojang"));
                }
            });
        } else if (button.id == 6) {
            mc.displayGuiScreen(new GuiLoginMicrosoftAccount(this) {
                @Override
                protected void onLogin(@NotNull MicrosoftAuth.Data data) {
                    AltManager.INSTANCE.getAltList().add(new MicrosoftAlt(data.getUserName(), data.getRefreshToken()));
                }
            });
        } else if (button.id == 7) {
            mc.displayGuiScreen(new GuiOriginalLogin(this,true));
        } else if (button.id == 8) {
            mc.displayGuiScreen(new GuiOriginalLogin(this,false));
        }

        super.actionPerformed(button);
    }

    @Override
    public void initGui() {
        buttonList.add(new GuiButton(0,205,height - 22,60,20,"返回"));
        buttonList.add(new GuiButton(1,270,height - 22,60,20,"增添"));
        buttonList.add(buttonLogin = new GuiButton(2,205,height - 44,60,20,"登录"));
        buttonList.add(buttonRemove = new GuiButton(3,205,height - 66,60,20,"删除"));
        buttonList.add(new GuiButton(4,270,height - 44,60,20,"直接登录"));
        buttonList.add(new GuiButton(5,270,height - 66,60,20,"微软登录"));
        buttonList.add(new GuiButton(6,335,height - 22,60,20,"添加微软账户"));
        buttonList.add(new GuiButton(7,335,height - 44,60,20,"原始登录"));
        buttonList.add(new GuiButton(8,335,height - 66,60,20,"添加原始账号"));
        super.initGui();
    }
}
