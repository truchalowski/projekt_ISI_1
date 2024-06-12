import { useEffect, useRef, useState } from 'react';
import { useMutation, useQuery } from 'react-query';

import { fetchEmployees } from '../../api/GetFetches';
import { createReservation } from '../../api/PostFetches';
import useGenerateDates from '../../hooks/useGenerateDates';
import { CreateReservationTypes, EmployeesType } from '../../types/Types';
import convertTime from '../../utils/convertTime';
import DayPicker from '../DayPicker/DayPicker';
import EmployeePicker from '../EmployeePicker/EmployeePicker';
import LoadingPageComponent from '../LoadingComponents/LoadingPageComponent';
import TimePicker from '../TimePicker/TimePicker';
import './Booking.css';

interface BookingProps {
  selectedServices: {
    id: number;
    name: string;
    duration: string;
    priceRange: string;
  };
}

export default function Booking({ selectedServices }: BookingProps) {
  const userId = +!+![] + +!+![];

  const { data, error, isFetching } = useQuery(['employees'], fetchEmployees);
  const mutation = useMutation(createReservation);

  const handleCreateReservation = (reservationData: CreateReservationTypes) => {
    if (reservationData.employeeId == null) {
      console.error('Employee ID is null');
      return;
    }

    mutation.mutate(reservationData, {
      onSuccess: (data) => {
        console.log('Reservation created successfully', data);
      },
      onError: (error) => {
        console.error('Error creating reservation', error);
      },
    });
  };

  const generateDates = useGenerateDates();
  const listOfDates = generateDates();

  const [selectedDate, setSelectedDate] = useState(listOfDates[0]);
  const [selectedTime, setSelectedTime] = useState<string | null>(null);
  const [selectedEmployee, setSelectedEmployee] =
    useState<EmployeesType | null>(null);
  const [isEmployeeSelect, setIsEmployeeSelect] = useState(false);

  const oneTimeUseEffect = useRef(true);

  useEffect(() => {
    if (data && data.length > 0 && oneTimeUseEffect.current) {
      setSelectedEmployee(data[0]);
      oneTimeUseEffect.current = false;
    }
  }, [data, setSelectedEmployee]);

  if (error) {
    console.error(error);
    return;
  }
  if (isFetching) {
    return <LoadingPageComponent />;
  }
  if (!data) {
    return;
  }

  return isEmployeeSelect ? (
    <div className="employee-picker">
      <EmployeePicker
        employeesData={data}
        setIsEmployeeSelect={setIsEmployeeSelect}
        setSelectedEmployee={setSelectedEmployee}
        setSelectedTime={setSelectedTime}
      />
    </div>
  ) : (
    <div className="booking">
      <DayPicker
        selectedDate={selectedDate}
        setSelectedDate={setSelectedDate}
        setSelectedTime={setSelectedTime}
      />
      <hr />
      <TimePicker
        selectedDate={selectedDate}
        selectedTime={selectedTime}
        setSelectedTime={setSelectedTime}
        employeeId={selectedEmployee?.id}
      />
      <hr />
      <div className="booking__service">
        <p className="booking__service-name">{selectedServices.name}</p>
        <div className="booking__service-details">
          <p className="booking__service-price">
            {selectedServices.priceRange} PLN
          </p>
          <p className="booking__service-duration">
            {convertTime(selectedServices.duration)}
          </p>
        </div>
      </div>
      <div className="booking__employee">
        <p>
          <span style={{ color: 'gray' }}>Employee: </span>
          {selectedEmployee?.firstName} {selectedEmployee?.lastName}
        </p>
        <button
          onClick={() => setIsEmployeeSelect(true)}
          className="booking__change-btn"
        >
          Change
        </button>
      </div>
      <hr />
      <button
        onClick={async () => {
          handleCreateReservation({
            reservationDate: selectedDate + 'T' + selectedTime,
            userId: userId,
            serviceId: selectedServices.id,
            employeeId: selectedEmployee?.id,
          });
        }}
        className="booking__btn"
        style={!selectedTime ? { opacity: '0.1' } : undefined}
        disabled={!selectedTime}
      >
        Book
      </button>
    </div>
  );
}
