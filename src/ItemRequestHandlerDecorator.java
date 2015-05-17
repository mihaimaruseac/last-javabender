import java.io.IOException;

public abstract class ItemRequestHandlerDecorator extends ItemRequestHandler {
	protected ItemRequestHandler rh;

	public ItemRequestHandlerDecorator(ItemRequestHandler rh) {
		if (rh instanceof ItemRequestHandlerDecorator)
			this.rh = ((ItemRequestHandlerDecorator)rh).rh;
		else
			this.rh = rh;

		this.in = rh.in;
		this.out = rh.out;
	}

	/* decorated methods, override them if needed */
	@Override
	protected void getBody() throws IOException {
		rh.getBody();
	}

	@Override
	protected void sendStatusCode() {
		rh.sendStatusCode();
	}

	@Override
	protected void sendLengthHeader() {
		rh.sendLengthHeader();
	}

	@Override
	protected void sendOtherHeaders() {
		rh.sendOtherHeaders();
	}

	@Override
	protected void sendBody() {
		rh.sendBody();
	}

	@Override
	protected void parseBody() throws IOException {
		rh.parseBody();
	}

	@Override
	protected void setNewBody(byte newBody[], int sz) {
		rh.setNewBody(newBody, sz);
	}
}
