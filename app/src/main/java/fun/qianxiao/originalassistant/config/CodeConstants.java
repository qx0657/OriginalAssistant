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

    public static final String CODE_1 = TOAST_CODE_TEXT;
    public static final String CODE_2 = "";
    public static final String CODE_3 = "";
    public static final String CODE_4 = "";

    public static final String CODE_1_NAME_DEFAULT = "弹窗代码";
    public static final String CODE_2_NAME_DEFAULT = "代码2";
    public static final String CODE_3_NAME_DEFAULT = "代码3";
    public static final String CODE_4_NAME_DEFAULT = "代码4";
}
