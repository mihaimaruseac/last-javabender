public class KeepAliveRequestHandler extends ItemRequestHandlerDecorator {
	public KeepAliveRequestHandler(ItemRequestHandler rh) {
		super(rh);
	}

	@Override
	protected void sendOtherHeaders() {
		super.sendOtherHeaders();
		out.println("Connection: keep-alive");
	}
}
