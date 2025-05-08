import { ValidatorConstraint, ValidatorConstraintInterface, ValidationArguments } from 'class-validator';

//questo decoretor controlla:
// che ci sia o "municipality" oppure "coordinates" e "radius"
//dove coordinates è latitude e longitude
@ValidatorConstraint({ name: 'MunicipalityCoordinatesValidator', async: false })
export class MunicipalityCoordinatesValidator implements ValidatorConstraintInterface {
  validate(_: any, args: ValidationArguments) {
    const obj = args.object as any;

    const hasMunicipality = !!obj.municipality;
    const hasCoordinates = !!obj.latitude && !!obj.longitude;
    const hasRadius = obj.radius !== null && obj.radius !== undefined;

    return (
      (hasMunicipality && hasCoordinates && hasRadius) ||
      (!hasMunicipality && !hasCoordinates && !hasRadius)
    );
  }

  defaultMessage(args: ValidationArguments) {
    return ` Bad request: Either "municipality" either "coordinates" and "radius" must be provided, or none of them.`;
  }
}
