package com.vitalsport.photos.validator;

import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.Test;

public class InputValidatorTest {

    private String message = "message";
    private InputValidator inputValidator = new InputValidator();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void notThrowsIllegalArgumentExceptionOnInvalidPredicate() {
        inputValidator.validate((elem) -> elem, false, message);
    }

    @Test
    public void throwsIllegalArgumentExceptionOnValidPredicate() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(message);

        inputValidator.validate((elem) -> elem, true, message);
    }
}