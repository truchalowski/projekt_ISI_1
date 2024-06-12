import { MdDeleteForever } from 'react-icons/md';
import Swal from 'sweetalert2';

import convertTime from '../../utils/convertTime';
import './Reservations.css';

const testoweDane = [
  {
    id: 1,
    firstName: 'Adam',
    lastName: 'Malysz',
    priceRange: '21-37',
    name: 'haircut',
    duration: '00:30:00',
    date: '2024-06-12 12:00:00',
  },
  {
    id: 2,
    firstName: 'Ewa',
    lastName: 'Wachowicz',
    priceRange: '22-38',
    name: 'haircuts',
    duration: '01:30:00',
    date: '2024-06-24 05:00:00',
  },
];

export default function ReservationsActive() {
  const handleClick = () => {
    Swal.fire({
      title: 'Are you sure?',
      text: "You won't be able to revert this!",
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Yes, do it!',
      cancelButtonText: 'No, cancel!',
    }).then((result) => {
      if (result.isConfirmed) {
        console.log('ok');
      }
    });
  };

  return (
    <div className="reservations-active">
      <div className="reservations-active__container">
        <div>
          <h1 className="reservations-active__header">Active bookings</h1>
          {testoweDane.map((reservation) => {
            return (
              <div key={reservation.id}>
                <div className="reservation">
                  <div className="reservation__details">
                    <h2 className="reservation__date">{reservation.date}</h2>
                    <div className="reservation__name">
                      <p className="reservation__name-part">
                        {reservation.name.slice(0, 1).toUpperCase() +
                          reservation.name.slice(1)}
                      </p>
                      <p className="reservation__separator">-</p>
                      <p className="reservation__full-name">
                        {reservation.firstName + ' ' + reservation.lastName}
                      </p>
                    </div>
                    <p className="reservation__duration">
                      {convertTime(reservation.duration)}
                    </p>
                    <p className="reservation__price">{reservation.priceRange} PLN</p>
                  </div>
                  <button onClick={handleClick} className="reservation__remove-button">
                    <MdDeleteForever className="reservation__remove-icon" />
                    Remove
                  </button>
                </div>
                <hr className="reservation__separator-line"/>
              </div>
            );
          })}
        </div>
        <div>
          <h1 className="reservations-active__header">Booking history</h1>
          {testoweDane.map((reservation) => {
            return (
              <div key={reservation.id}>
                <div className="reservation">
                  <div className="reservation__details">
                    <h2 className="reservation__date">{reservation.date}</h2>
                    <div className="reservation__name">
                      <p className="reservation__name-part">
                        {reservation.name.slice(0, 1).toUpperCase() +
                          reservation.name.slice(1)}
                      </p>
                      <p className="reservation__separator">-</p>
                      <p className="reservation__full-name">
                        {reservation.firstName + ' ' + reservation.lastName}
                      </p>
                    </div>
                    <p className="reservation__duration">
                      {convertTime(reservation.duration)}
                    </p>
                    <p className="reservation__price">{reservation.priceRange} PLN</p>
                  </div>
                </div>
                <hr className="reservation__separator-line"/>
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}
