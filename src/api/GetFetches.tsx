const fetchServices = async () => {
  const response = await fetch('http://localhost:3000/services');
  if (!response.ok) {
    throw new Error('Network response was not ok');
  }
  return response.json();
};

const fetchEmployees = async () => {
  const response = await fetch('http://localhost:3000/employees');
  if (!response.ok) {
    throw new Error('Network response was not ok');
  }
  return response.json();
};
//http://localhost:3000/reservations/available?date=2024-06-30&employeeId=1
const fetchAvaiableHours = async (
  date: string | null,
  employeeId: number | undefined,
) => {
  if (!employeeId) return;
  const response = await fetch(
    `http://localhost:3000/reservations/available?date=${date}&employeeId=${employeeId}`,
  );
  if (!response.ok) {
    throw new Error('Network response was not ok');
  }
  return response.json();
};

export { fetchServices, fetchEmployees, fetchAvaiableHours };
