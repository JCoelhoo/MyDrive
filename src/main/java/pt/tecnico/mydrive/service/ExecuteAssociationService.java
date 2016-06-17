package pt.tecnico.mydrive.service;

public class ExecuteAssociationService extends MyDriveService{
	
	private String filename;
	private String extension;
	 
	public ExecuteAssociationService(String filename) {
		this.filename=filename;
	}

	
	 public final void dispatch() { 
		 extension = getMyDrive().getExtensionByFilename(filename);
     }


	public final String result() {
		return "txt";
	}

}
