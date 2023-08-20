import androidx.compose.runtime.Composable;
import cn.hutool.core.swing.clipboard.ClipboardListener;
import cn.hutool.core.swing.clipboard.ClipboardUtil;
import cn.hutool.http.HttpUtil;
import util.PackageScanUtil;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;

public class Test {
    public static void main(String[] args) throws ClassNotFoundException {
        System.out.println(PackageScanUtil.scan("view"));
        Class<?> aClass = Class.forName("view.ConnectViewKt");
        PackageScanUtil.handleMethodAnnotation(aClass.getDeclaredMethods(), Composable.class, ((method, annotation) -> {
            System.out.println(method.getName());
        }));
    }
}
