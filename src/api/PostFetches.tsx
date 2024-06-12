import axios from 'axios';
import { AxiosError } from 'axios';

import { CreateReservationTypes } from '../types/Types';

// const reservationData = {
//   reservationDate: "2024-06-13T10:30:00",
//   userId: 2,
//   serviceId: 1,
//   employeeId: 1,
// };

const createReservation = async (reservationData: CreateReservationTypes) => {
  try {
    const response = await axios.post(
      'http://localhost:3000/reservations/create',
      reservationData,
    );
    return response.data;
  } catch (error) {
    console.error(
      'Error creating reservation:',
      (error as AxiosError).response
        ? (error as AxiosError).response?.data
        : (error as Error).message,
    );
    throw error;
  }
};

export { createReservation };
