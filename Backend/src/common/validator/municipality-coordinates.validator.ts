import { ValidatorConstraint, ValidatorConstraintInterface, ValidationArguments } from 'class-validator';

@ValidatorConstraint({ name: 'MunicipalityCoordinatesValidator', async: false })
export class MunicipalityCoordinatesValidator implements ValidatorConstraintInterface {
  validate(_: any, args: ValidationArguments) {
    const obj = args.object as any;

    const hasMunicipality = !!obj.municipality;
    const hasCoordinates = !!obj.coordinates;
    const hasRadius = obj.radius !== null && obj.radius !== undefined;

    return (
      (hasMunicipality && hasCoordinates && hasRadius) ||
      (!hasMunicipality && !hasCoordinates && !hasRadius)
    );
  }

  defaultMessage(args: ValidationArguments) {
    return `Se "municipality" è presente, allora anche "coordinates" e "radius" devono esserlo, e viceversa.`;
  }
}
