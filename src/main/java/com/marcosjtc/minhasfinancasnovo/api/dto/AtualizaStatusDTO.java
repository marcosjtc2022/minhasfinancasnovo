package com.marcosjtc.minhasfinancasnovo.api.dto;

import java.util.Objects;

public class AtualizaStatusDTO {
	
	private String status;

	public AtualizaStatusDTO(String status) {
		this.status = status;
	}
	
	public AtualizaStatusDTO() {
	
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "AtualizaStatusDTO [status=" + status + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(status);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AtualizaStatusDTO other = (AtualizaStatusDTO) obj;
		return Objects.equals(status, other.status);
	}
	
	

	

}
