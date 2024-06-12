interface ServiceTypes {
  id: number;
  name: string;
  duration: string;
  priceRange: string;
}

interface EmployeesType {
  id: number;
  firstName: string;
  lastName: string;
}

interface CreateReservationTypes {
  reservationDate: string;
  userId: number | undefined;
  serviceId: number;
  employeeId: number | undefined;
}

export type { ServiceTypes, EmployeesType, CreateReservationTypes };
