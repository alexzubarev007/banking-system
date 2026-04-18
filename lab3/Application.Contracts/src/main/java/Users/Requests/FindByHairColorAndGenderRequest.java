package Users.Requests;

import Users.Gender;

public record FindByHairColorAndGenderRequest(String hairColor,
                                              Gender gender) { }
