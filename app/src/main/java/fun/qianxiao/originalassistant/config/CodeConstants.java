package fun.qianxiao.originalassistant.config;

/**
 * CodeConstants
 *
 * @Author QianXiao
 * @Date 2023/5/10
 */
public class CodeConstants {
    public static final String TOAST_CODE_TEXT = "const-string v0, \"弹窗内容\"\n" +
            "\n" +
            "    const/4 v1, 0x0\n" +
            "\n" +
            "    invoke-static {p0, v0, v1}, Landroid/widget/Toast;->makeText(Landroid/content/Context;Ljava/lang/CharSequence;I)Landroid/widget/Toast;\n" +
            "\n" +
            "    move-result-object v0\n" +
            "\n" +
            "    invoke-virtual {v0}, Landroid/widget/Toast;->show()V";
}
