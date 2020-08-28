package fr.entasia.sanctions;

public enum SanctionTypes {
	Ban(0),
	Mute(1),
	Kick(2),

	;

	public int id;

	SanctionTypes(int id) {
		this.id = id;
	}

	public static SanctionTypes getByID(int id){
		for(SanctionTypes st : values()){
			if(st.id==id)return st;
		}
		return null;
	}
}
