import React, { useState } from "react";
import Navbar from "../Navbar";
import { IoIosArrowBack, IoIosArrowForward } from "react-icons/io";

const CalendarPage: React.FC = () => {
  const [selectedDate, setSelectedDate] = useState<Date | null>(null);
  const [currentMonth, setCurrentMonth] = useState(new Date());
  const today = new Date(); // Define today

  const generateCalendarMatrix = () => {
    const startOfMonth = new Date(currentMonth.getFullYear(), currentMonth.getMonth(), 1);
    const endOfMonth = new Date(currentMonth.getFullYear(), currentMonth.getMonth() + 1, 0);
    const startDay = startOfMonth.getDay();
    const daysInMonth = endOfMonth.getDate();
    
    let matrix: { date: Date; currentMonth: boolean }[] = [];

    // Fill days from the previous month
    for (let i = startDay - 1; i >= 0; i--) {
      const date = new Date(startOfMonth);
      date.setDate(date.getDate() - i - 1);
      matrix.push({ date, currentMonth: false });
    }

    // Fill days in the current month
    for (let i = 1; i <= daysInMonth; i++) {
      const date = new Date(currentMonth.getFullYear(), currentMonth.getMonth(), i);
      matrix.push({ date, currentMonth: true });
    }

    // Fill remaining days to complete 7x5 grid
    for (let i = 1; matrix.length < 35; i++) {
      const date = new Date(currentMonth.getFullYear(), currentMonth.getMonth() + 1, i);
      matrix.push({ date, currentMonth: false });
    }

    return matrix;
  };

  const handlePreviousMonth = () => {
    setCurrentMonth(new Date(currentMonth.getFullYear(), currentMonth.getMonth() - 1, 1));
  };

  const handleNextMonth = () => {
    setCurrentMonth(new Date(currentMonth.getFullYear(), currentMonth.getMonth() + 1, 1));
  };

  const handleDateClick = (date: Date) => {
    setSelectedDate(date);
  };

  const closePopup = () => {
    setSelectedDate(null);
  };

  const matrix = generateCalendarMatrix();

  return (
    <div>
      <Navbar />
      <div className="p-4 pt-10 text-center">
        <div className="flex items-center justify-center gap-4">
          <button onClick={handlePreviousMonth} className="text-gray-600 hover:text-black">
            <IoIosArrowBack size={24} />
          </button>
          <h2 className="text-2xl font-semibold">
            {currentMonth.toLocaleString("default", { month: "long" })} {currentMonth.getFullYear()}
          </h2>
          <button onClick={handleNextMonth} className="text-gray-600 hover:text-black">
            <IoIosArrowForward size={24} />
          </button>
        </div>
        <div className="grid grid-cols-7 gap-2 mt-4 text-center">
          {["Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"].map((day) => (
            <div key={day} className="font-bold text-gray-700">
              {day}
            </div>
          ))}
          {matrix.map(({ date, currentMonth }, index) => (
            <div
              key={index}
              onClick={() => handleDateClick(date)}
              className={`p-4 rounded cursor-pointer h-28 ${
                currentMonth ? "bg-white text-black" : "text-gray-400"
              } ${selectedDate && selectedDate.getTime() === date.getTime() && "border-2 border-blue-500"}
                ${date.toDateString() === today.toDateString() ? "bg-lime-100" : ""}`}
            >
              {date.getDate()}
            </div>
          ))}
        </div>
      </div>
      {selectedDate && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center">
          <div className="bg-white  rounded shadow-lg h-[500px] w-[1000px]">
            <div className="flex justify-between p-4 items-center bg-gray-300">
              <h3 className="text-xl font-bold">
                {selectedDate.toLocaleDateString("en-EN", {
                  day: "numeric",
                  month: "long",
                  year: "numeric",
                })}
              </h3>
              <div className="space-x-2">
                <button className="bg-green-500 text-white px-4 py-2 rounded-full">Save</button>
                <button onClick={closePopup} className=" px-4 py-2 rounded-full border border-gray-600">
                  Cancel
                </button>
              </div>
            </div>
            <div className="mt-4">
              {/* info */}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default CalendarPage;
