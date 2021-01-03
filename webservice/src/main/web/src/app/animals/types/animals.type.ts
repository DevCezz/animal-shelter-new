import {AnimalKind} from "../enums/animal-kind.enum";

export type AnimalShortInfo = {
  id: number;
  name: string;
  kind: AnimalKind;
  age: number;
  inShelter: boolean;
}
