package io.github.gms.secure.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsernamePasswordPair implements Serializable {

	private static final long serialVersionUID = -315870023488483248L;
	private String username;
	private String credential;
}