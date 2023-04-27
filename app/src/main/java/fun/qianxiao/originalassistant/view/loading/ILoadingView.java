package fun.qianxiao.originalassistant.view.loading;

/**
 * ILoadingView
 *
 * @Author QianXiao
 * @Date 2023/3/10
 */
public interface ILoadingView {
    /**
     * Show Dialog
     *
     * @param msg Message
     */
    void openLoadingDialog(String msg);

    /**
     * Close Dialog
     */
    void closeLoadingDialog();
}
