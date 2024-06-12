import { useCallback, useEffect, useRef, useState } from 'react';
import { useQuery } from 'react-query';
import type { Swiper as SwiperTypes } from 'swiper';
import 'swiper/css';
import { Swiper, SwiperSlide } from 'swiper/react';

import { fetchAvaiableHours } from '../../api/GetFetches';
import LoadingPageComponent from '../LoadingComponents/LoadingPageComponent';
import './TimePicker.css';

interface SwiperInterface {
  isBeginning: boolean;
  isEnd: boolean;
  activeIndex: number;
}

type SwiperRef = {
  swiper: SwiperTypes;
};

interface TimePickerProps {
  selectedDate: string | null;
  selectedTime: string | null;
  setSelectedTime: React.Dispatch<React.SetStateAction<string | null>>;
  employeeId: number | undefined;
}

export default function TimePicker({
  selectedDate,
  selectedTime,
  setSelectedTime,
  employeeId,
}: TimePickerProps) {
  const { data, error, isFetching } = useQuery(
    ['availableHours', selectedDate, employeeId],
    () => fetchAvaiableHours(selectedDate, employeeId),
    {
      enabled: !!selectedDate,
    },
  );

  const [windowWidth, setWindowWidth] = useState(window.innerWidth);
  const [atBeginning, setAtBeginning] = useState(true);
  const [atEnd, setAtEnd] = useState(false);

  const swiperRef = useRef<SwiperRef | null>(null);

  console.log(selectedDate);

  const handleResize = useCallback(() => {
    setWindowWidth(window.innerWidth);
  }, []);

  useEffect(() => {
    window.addEventListener('resize', handleResize);

    return () => {
      window.removeEventListener('resize', handleResize);
    };
  }, [handleResize]);

  const handleSlideChange = (swiper: SwiperInterface) => {
    setAtBeginning(swiper.isBeginning);
    setAtEnd(swiper.isEnd);
  };

  function renderTime(item: string) {
    return (
      <div
        className={`time-component__button ${
          selectedTime === item ? 'time-component__time--selected' : ''
        }`}
        key={item}
      >
        <div className="time-element">
          <div className="time-element__time">
            <h2 className="time-element__heading time-element__heading--h1">
              {item[0] === '0'
                ? item.slice(1).split(':').slice(0, 2).join(':')
                : item.split(':').slice(0, 2).join(':')}
            </h2>
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    console.error(error);
    return null;
  }
  if (isFetching) {
    return <LoadingPageComponent />;
  }
  if (!data) {
    return null;
  }

  return (
    <div className="timepicker">
      <div className="timepicker__container">
        <button
          className={`timepicker__container-button ${atBeginning ? 'timepicker__container-button--disabled' : ''}`}
          disabled={atBeginning}
          onClick={() => {
            if (swiperRef.current && !atBeginning) {
              swiperRef.current.swiper.slidePrev();
            }
          }}
        >
          {'<'}
        </button>
        <Swiper
          ref={swiperRef}
          onSlideChange={(swiper) => handleSlideChange(swiper)}
          spaceBetween={10}
          slidesPerView={windowWidth > 768 ? 5 : windowWidth > 480 ? 3 : 2}
          slidesPerGroup={windowWidth > 768 ? 5 : windowWidth > 480 ? 3 : 2}
        >
          {data.map((time: string, index: number) => (
            <SwiperSlide
              key={index}
              onClick={() => setSelectedTime(time)}
              style={{ cursor: 'pointer' }}
            >
              {renderTime(time)}
            </SwiperSlide>
          ))}
        </Swiper>
        <button
          className={`timepicker__container-button ${atEnd ? 'timepicker__container-button--disabled' : ''}`}
          disabled={atEnd}
          onClick={() => {
            if (swiperRef.current && !atEnd) {
              swiperRef.current.swiper.slideNext();
            }
          }}
        >
          {'>'}
        </button>
      </div>
    </div>
  );
}
