package burp;

import com.sun.org.apache.xpath.internal.functions.FuncFalse;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.io.PrintWriter;
import java.util.List;
import javax.swing.JMenuItem;


public class BurpExtender implements IBurpExtender,IProxyListener, IContextMenuFactory, ActionListener {

	private IBurpExtenderCallbacks callbacks;
	private static String[] colorArray = new String[] {"red", "orange", "yellow", "green", "cyan", "blue", "pink", "magenta", "gray"};
	private static IMessageEditorTab HaETab;
	private static PrintWriter stdout;
	private static boolean isEnable = true;
	IExtensionHelpers helpers = null;


	@Override
	public void registerExtenderCallbacks(final IBurpExtenderCallbacks callbacks)
	{
		this.callbacks = callbacks;
		this.helpers = callbacks.getHelpers();

		// 设置插件名字
		callbacks.setExtensionName("Mark Different Browsers");

		// 定义输出
		stdout = new PrintWriter(callbacks.getStdout(), true);
		stdout.println("Author: xiaoxiaoleo");
		stdout.println("Repo: https://github.com/xiaoxiaoleo/Mark-Different-Browsers");

		callbacks.registerProxyListener(BurpExtender.this);
		callbacks.registerContextMenuFactory(BurpExtender.this);
	}


	@Override
	public void processProxyMessage(final boolean messageIsRequest, final IInterceptedProxyMessage proxyMessage) {
		if (isEnable == false){
			return;
		}
		if (messageIsRequest == false){
			return;
		}

		String headerName = "browser-color:";
		String hColor = null;

		IRequestInfo rqInfo = helpers.analyzeRequest(proxyMessage.getMessageInfo());
		// retrieve all headers
		ArrayList<String> headers = (ArrayList<String>) rqInfo.getHeaders();
		for (int i = 0; i < headers.size(); i++) {
			if (((String) headers.get(i)).startsWith(headerName)) {
				String lst[] = headers.get(i).split(":");
				hColor = lst[1].replace(" ", "");
			}
		}
		proxyMessage.getMessageInfo().setHighlight(hColor);
	}

	@Override
	public List<JMenuItem> createMenuItems(IContextMenuInvocation invocation) {
		if(invocation.getInvocationContext() == invocation.CONTEXT_PROXY_HISTORY){
			List<JMenuItem> menu = new ArrayList<JMenuItem>();
			JMenuItem jItem = null;
			if(isEnable){
				jItem = new JMenuItem("Mark Different Browsers (Running): Click to Disable ");

			}else{
				jItem = new JMenuItem("Mark Different Browsers (Stopped): Click to Enable ");
			}
			jItem.addActionListener(this);
			menu.add(jItem);
			return menu;
		}

		return null;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		isEnable = !isEnable;

	}

}