import React, { useState, useEffect } from "react";
import Navbar from "../Navbar";
import { IoIosArrowBack, IoIosArrowForward, IoIosClose } from "react-icons/io";
import {
  getAllExecutionStatuses,
  getEmployeeById,
  getTemplateById,
} from "@/api";

interface Template {
  id: string;
  name: string;
  steps: { id: string; description: string }[];
}

interface Process {
  processId: string;
  name: string;
  statuses: {
    employeeId: string;
    stepId: string;
    deadline: string;
  }[];
}

const CalendarPage: React.FC = () => {
  const [selectedDate, setSelectedDate] = useState<Date | null>(null);
  const [currentMonth, setCurrentMonth] = useState(new Date());
  const [processes, setProcesses] = useState<Process[]>([]);
  const [employeeNames, setEmployeeNames] = useState<Map<string, string>>(new Map());
  const [processTemplates, setProcessTemplates] = useState<Map<string, Template>>(new Map());
  const today = new Date();

  useEffect(() => {
    const fetchProcesses = async () => {
      const processes = await getAllExecutionStatuses();
      setProcesses(processes);

      const employeeIds = new Set<string>();
      const templateIds = new Set<string>();
      processes.forEach((process: Process) => {
        process.statuses.forEach((status) => {
          employeeIds.add(status.employeeId);
        });
        templateIds.add(process.processId);
      });

      const namesMap = new Map<string, string>();
      for (const employeeId of employeeIds) {
        const employee = await getEmployeeById(employeeId);
        namesMap.set(employeeId, `${employee.lastName} ${employee.firstName} ${employee.middleName}`);
      }

      const templateMap = new Map<string, Template>();
      for (const templateId of templateIds) {
        const template = await getTemplateById(templateId);
        templateMap.set(templateId, template);
      }

      setEmployeeNames(namesMap);
      setProcessTemplates(templateMap);
    };

    fetchProcesses();
  }, []);

  const generateCalendarMatrix = () => {
    const startOfMonth = new Date(currentMonth.getFullYear(), currentMonth.getMonth(), 1);
    const endOfMonth = new Date(currentMonth.getFullYear(), currentMonth.getMonth() + 1, 0);
    const startDay = startOfMonth.getDay();
    const daysInMonth = endOfMonth.getDate();

    let matrix: { date: Date; currentMonth: boolean; hasDeadline: boolean; deadlines: string[] }[] = [];

    // Fill days from the previous month
    for (let i = startDay - 1; i >= 0; i--) {
      const date = new Date(startOfMonth);
      date.setDate(date.getDate() - i - 1);
      matrix.push({ date, currentMonth: false, hasDeadline: false, deadlines: [] });
    }

    // Fill days in the current month
    for (let i = 1; i <= daysInMonth; i++) {
      const date = new Date(currentMonth.getFullYear(), currentMonth.getMonth(), i);
      const deadlines = processes.flatMap((process) =>
        process.statuses
          .filter((status) => new Date(status.deadline).toDateString() === date.toDateString())
          .map((status) => `${process.name} - ${employeeNames.get(status.employeeId)}`)
      );
      matrix.push({ date, currentMonth: true, hasDeadline: deadlines.length > 0, deadlines });
    }

    // Fill remaining days to complete 7x5 grid
    for (let i = 1; matrix.length < 35; i++) {
      const date = new Date(currentMonth.getFullYear(), currentMonth.getMonth() + 1, i);
      matrix.push({ date, currentMonth: false, hasDeadline: false, deadlines: [] });
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

  const getProcessesForDate = (date: Date) => {
    return processes.map((process) => {
      const filteredStatuses = process.statuses.filter(
        (status) => new Date(status.deadline).toDateString() === date.toDateString()
      );
      return { ...process, statuses: filteredStatuses };
    }).filter(process => process.statuses.length > 0);
  };

  return (
    <div>
      <Navbar />
      <div className="p-4 pt-10 text-center">
        <div className="flex items-center justify-center gap-4">
          <button onClick={handlePreviousMonth} className="text-gray-600 hover:text-black">
            <IoIosArrowBack size={24} />
          </button>
          <h2 className="text-2xl font-semibold">
            {currentMonth.toLocaleString("ru-RU", { month: "long" })}{" "}
            {currentMonth.getFullYear()}
          </h2>
          <button onClick={handleNextMonth} className="text-gray-600 hover:text-black">
            <IoIosArrowForward size={24} />
          </button>
        </div>
        <div className="grid grid-cols-7 gap-2 mt-4 text-center">
          {["Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"].map((day) => (
            <div key={day} className="font-bold text-gray-700">
              {day}
            </div>
          ))}
          {matrix.map(({ date, hasDeadline, deadlines }, index) => {
            const isToday = date.toDateString() === today.toDateString();
            return (
              <div
                key={index}
                onClick={() => handleDateClick(date)}
                className={`p-2 rounded cursor-pointer h-28 relative flex flex-col justify-between
                            ${isToday ? "bg-green-200 font-bold" : ""}
                            ${hasDeadline ? "border-2 border-red-300" : ""}`}
              >
                <div className="flex-grow flex items-center justify-center">
                  {date.getDate()}
                </div>
                {hasDeadline && (
                  <div className="absolute top-2 right-2 w-3 h-3 bg-red-500 rounded-full"></div>
                )}
                {deadlines.length > 0 && (
                  <div className="text-xs mt-1 flex-grow overflow-hidden">
                    {deadlines.map((deadline, idx) => (
                      <p key={idx} className="break-words" title={deadline}>
                        {deadline}
                      </p>
                    ))}
                  </div>
                )}
              </div>
            );
          })}
        </div>
      </div>
      {selectedDate && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex justify-center items-center">
          <div className="bg-white rounded shadow-lg h-[500px] w-[1000px] overflow-hidden">
            <div className="flex justify-between p-4 items-center bg-gray-300 sticky top-0 z-10">
              <h3 className="text-xl font-bold">
                {selectedDate.toLocaleDateString("ru-RU", {
                  day: "numeric",
                  month: "long",
                  year: "numeric",
                })}
              </h3>
              <button onClick={closePopup} className="text-gray-600 hover:text-black">
                <IoIosClose size={24} />
              </button>
            </div>
            <div className="mt-4 p-4 overflow-y-auto h-[400px]">
              {getProcessesForDate(selectedDate).map((process) => (
                <div key={process.processId} className="mb-4">
                  <h4 className="text-lg font-semibold">{process.name}</h4>
                  <ul>
                    {process.statuses.map((status) => {
                      const template = processTemplates.get(process.processId);
                      const stepDescription = template?.steps.find(
                        (step) => step.id === status.stepId
                      )?.description;

                      return (
                        <li key={status.employeeId} className="border-b pb-2 mt-2">
                          <p>{employeeNames.get(status.employeeId)}</p>
                          <p>Текущий шаг: {stepDescription}</p>
                          <p>
                            Дедлайн: {new Date(status.deadline).toLocaleDateString()}
                          </p>
                        </li>
                      );
                    })}
                  </ul>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default CalendarPage;
