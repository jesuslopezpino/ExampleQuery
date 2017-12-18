package com.polvisoft.exampleQuery.domain;

import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BasicDTO<PK> {

	public static final String PK = "pk";

	public BasicDTO() {
		super();
	}

	public abstract PK getPk();

	public abstract void setPk(PK pk);

	@Override
	public String toString() {
		return "BasicDTO [pk=" + this.getPk() + "]";
	}

	public abstract String toStringDebug();

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof BasicDTO) {
			BasicDTO other = (BasicDTO) obj;
			if (other.getPk().equals(this.getPk())) {
				return true;
			}
		}
		return super.equals(obj);
	}

}
