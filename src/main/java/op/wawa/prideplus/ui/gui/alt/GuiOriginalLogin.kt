package op.wawa.prideplus.ui.gui.alt

import op.wawa.prideplus.Pride
import op.wawa.prideplus.ui.font.FontManager
import op.wawa.prideplus.ui.gui.alt.altimpl.OriginalAlt
import op.wawa.prideplus.ui.notification.NotificationManager
import op.wawa.prideplus.ui.notification.NotificationType
import com.mojang.realmsclient.gui.ChatFormatting
import net.minecraft.client.gui.GuiButton
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.gui.GuiTextField
import net.minecraft.util.Session
import java.util.*

class GuiOriginalLogin(private val parentScreen : GuiScreen,private val direct : Boolean) : GuiScreen() {
    private lateinit var userName : GuiTextField
    private lateinit var accessToken : GuiTextField
    private lateinit var uuid : GuiTextField
    private lateinit var legacyButton : GuiButton
    private lateinit var mojangButton : GuiButton

    private var selectedType = "mojang"
    private var status = "${ChatFormatting.YELLOW}等待中..."

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)

        val x = width.toFloat() / 2f
        val y = height.toFloat() / 2f

        FontManager.default16.drawCenteredStringWithShadow2(status, x.toDouble(),y - 50.0,-1)

        userName.drawTextBox()
        accessToken.drawTextBox()
        uuid.drawTextBox()

        if (!userName.isFocused && userName.text.isNullOrEmpty()) {
            FontManager.default16.drawStringWithShadow("${ChatFormatting.DARK_GRAY}${ChatFormatting.ITALIC}UserName",x - 77.0,y - 30.0,-1)
        }

        if (!accessToken.isFocused && accessToken.text.isNullOrEmpty()) {
            FontManager.default16.drawStringWithShadow("${ChatFormatting.DARK_GRAY}${ChatFormatting.ITALIC}AccessToken",x - 77.0,y - 5.0,-1)
        }

        if (!uuid.isFocused && uuid.text.isNullOrEmpty()) {
            FontManager.default16.drawStringWithShadow("${ChatFormatting.DARK_GRAY}${ChatFormatting.ITALIC}UUID",x - 77.0,y + 20.0,-1)
        }

        NotificationManager.render()
    }

    override fun keyTyped(typedChar: Char, keyCode: Int) {
        super.keyTyped(typedChar, keyCode)

        userName.textboxKeyTyped(typedChar,keyCode)
        accessToken.textboxKeyTyped(typedChar,keyCode)
        uuid.textboxKeyTyped(typedChar,keyCode)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)

        userName.mouseClicked(mouseX,mouseY,mouseButton)
        accessToken.mouseClicked(mouseX,mouseY,mouseButton)
        uuid.mouseClicked(mouseX,mouseY,mouseButton)
    }

    override fun actionPerformed(button: GuiButton?) {
        super.actionPerformed(button)

        when(button?.id) {
            0 -> {
                mc.displayGuiScreen(parentScreen)
            }
            1 -> {
                if (direct) {
                    mc.session = Session(userName.text, uuid.text, accessToken.text, selectedType)
                    status = "${ChatFormatting.GREEN}登录成功! Type:$selectedType UserName:${userName.text} AccessToken:${accessToken.text} UUID:${uuid.text}"
                } else {
                    AltManager.INSTANCE.addAlt(OriginalAlt(userName.text,accessToken.text,uuid.text,selectedType))
                    status = "${ChatFormatting.GREEN}添加成功! Type:$selectedType UserName:${userName.text} AccessToken:${accessToken.text} UUID:${uuid.text}"
                }
            }
            2 -> {
                selectedType = "legacy"
                legacyButton.enabled = false
                mojangButton.enabled = true
            }
            3 -> {
                selectedType = "mojang"
                legacyButton.enabled = true
                mojangButton.enabled = false
            }
            4 -> {
                uuid.text = UUID.randomUUID().toString().replace("-","")
            }
            5 -> {
                NotificationManager.post(NotificationType.INFO, "OriginalLogin", "你的用户名为:${mc.session.username}", 5F)
                NotificationManager.post(NotificationType.INFO, "OriginalLogin", "你的AccessToken为:${mc.session.token}", 5F)
                NotificationManager.post(NotificationType.INFO, "OriginalLogin", "你的UUID为:${mc.session.playerID}", 5F)

                Pride.LOGGER.info("OriginalLogin: UserName:${mc.session.username}")
                Pride.LOGGER.info("OriginalLogin: Token:${mc.session.token}")
                Pride.LOGGER.info("OriginalLogin: UUID:${mc.session.playerID}")
            }
        }
    }

    override fun initGui() {
        super.initGui()

        val x = width / 2
        val y = height / 2

        userName = GuiTextField(0, mc.fontRendererObj,x - 80,y - 35,160,20)
        userName.maxStringLength = 500

        accessToken = GuiTextField(1, mc.fontRendererObj,x - 80,y - 10,160,20)
        accessToken.maxStringLength = 500

        uuid = GuiTextField(2, mc.fontRendererObj,x - 80,y + 15,160,20)
        uuid.maxStringLength = 500

        buttonList.add(GuiButton(0,x + 40,y + 45,40,20,"返回"))
        buttonList.add(GuiButton(1,x - 80,y + 45,40,20,"登录"))

        legacyButton = GuiButton(2,x - 125,y - 35,40,20,"Legacy")
        buttonList.add(legacyButton)

        mojangButton = GuiButton(3,x - 125,y - 10,40,20,"Mojang")
        mojangButton.enabled = false
        buttonList.add(mojangButton)

        buttonList.add(GuiButton(4,x - 30,y + 45,50,20,"随机UUID"))

        buttonList.add(GuiButton(5,x - 80,y + 70,160,20,"获得当前信息"))
    }
}