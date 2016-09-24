package app.hibernatereference.chapter2.enums.attributeconverterexample.converter;

import app.hibernatereference.chapter2.enums.attributeconverterexample.enities.Gender;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class GenderConverter implements AttributeConverter<Gender, Character> {
    @Override
    public Character convertToDatabaseColumn(Gender gender) {
        if(gender == null) {
            return null;
        }
        return gender.getCode();
    }

    @Override
    public Gender convertToEntityAttribute(Character character) {
        if(character == null) {
            return null;
        }
        return Gender.fromCode(character);
    }
}
