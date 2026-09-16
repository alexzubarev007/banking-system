package users.requests;

import users.Gender;

public record FindByHairColorAndGenderRequest(String hairColor,
                                              Gender gender) { }
