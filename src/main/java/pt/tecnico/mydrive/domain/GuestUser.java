package pt.tecnico.mydrive.domain;

import pt.tecnico.mydrive.exception.CannotRemoveGuestUserException;
import pt.tecnico.mydrive.exception.ForbiddenMethodException;

public class GuestUser extends GuestUser_Base {

	public GuestUser(MyDrive myDrive) {
		super();
		if (myDrive == null)
			throw new ForbiddenMethodException("RootUser()");
		init("nobody", "Guest", "", (byte) (0b1111_1010));
		// home is set in mydrive.setup()
		setMydrive(myDrive);
	}

	@Override
	public boolean checkPassword(String password) {
		if (password.equals(""))
			return true;
		return false;

	}

	@Override
	public void setPassword(String pass) {
		return;
	}

	@Override
	protected boolean validPassword(String password) {
		return true;
	}

	@Override
	public long timeout() {
		return Long.MAX_VALUE;
	}

	@Override
	public void remove() throws CannotRemoveGuestUserException {
		throw new CannotRemoveGuestUserException();
	}

}
