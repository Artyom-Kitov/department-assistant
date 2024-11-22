import { useState } from "react";
import { Link } from "react-router-dom";
import Navbar from "../Navbar";
import { FaUserLarge } from "react-icons/fa6";
import { IoIosArrowDown, IoIosClose } from "react-icons/io";
import { HiArrowsUpDown } from "react-icons/hi2";
import { VscSettings } from "react-icons/vsc";
import { employeesData } from "@/fixtures/employeesData";


export default function EmployeesPage() {
  const [sortOrder, setSortOrder] = useState<string>("По фамилии ↑");
  const [filters, setFilters] = useState<string[]>([]);
  const [expanded, setExpanded] = useState<Set<number>>(new Set());
  const [showFilters, setShowFilters] = useState<boolean>(false);
  const [isSortDropdownVisible, setIsSortDropdownVisible] = useState<boolean>(false);

  const toggleFilter = (filter: string) => {
    setFilters((prevFilters) =>
      prevFilters.includes(filter)
        ? prevFilters.filter((item) => item !== filter)
        : [...prevFilters, filter]
    );
  };

  const resetFilters = () => setFilters([]);

  const toggleExpand = (id: number) => {
    setExpanded((prev) => {
      const newSet = new Set(prev);
      newSet.has(id) ? newSet.delete(id) : newSet.add(id);
      return newSet;
    });
  };

  const sortedEmployees = [...employeesData];
  if (sortOrder === "По фамилии ↑") {
    sortedEmployees.sort((a, b) => a.last_name.localeCompare(b.last_name));
  } else if (sortOrder === "По фамилии ↓") {
    sortedEmployees.sort((a, b) => b.last_name.localeCompare(a.last_name));
  }

  return (
    <div>
      <Navbar />

      <div className="flex flex-col items-center p-4">
      <div className="w-full max-w-4xl bg-white border border-gray-300 p-4 rounded-lg sticky top-24 z-10 flex items-center ">
          <div
            className="relative mr-4"
            onMouseEnter={() => setIsSortDropdownVisible(true)}
            onMouseLeave={() => setIsSortDropdownVisible(false)}
          >
            <button
              className="flex items-center px-4 py-2 bg-gray-200 rounded-md"
            >
              <HiArrowsUpDown className="text-lg text-gray-700 mr-2" />
              <span>{sortOrder}</span>
            </button>
            {isSortDropdownVisible && (
              <ul className="absolute right-0 bg-white border border-gray-300 rounded-md ">
                {["По фамилии ↑", "По фамилии ↓"].map((option) => (
                  <li
                    key={option}
                    className="px-4 py-2 hover:bg-gray-100 cursor-pointer"
                    onClick={() => {
                      setSortOrder(option);
                      setIsSortDropdownVisible(false);
                    }}
                  >
                    {option}
                  </li>
                ))}
              </ul>
            )}
          </div>

          <button
            className="flex items-center space-x-2 bg-gray-200 px-4 py-2 rounded-md hover:bg-gray-300"
            onClick={() => setShowFilters(true)}
          >
            <VscSettings className="text-lg text-gray-700" />
            <span>Фильтры</span>
          </button>
        </div>

        {showFilters && (
          <div className="fixed inset-0 z-50 bg-gray-900 bg-opacity-50 flex justify-center items-center">
            <div className="bg-white w-96 p-6 rounded-lg  relative">
              <button
                className="absolute top-2 right-2 text-gray-500 hover:text-gray-700"
                onClick={() => setShowFilters(false)}
              >
                <IoIosClose className="text-2xl" />
              </button>
              <h3 className="text-lg font-semibold mb-4">Фильтры</h3>
              <div className="space-y-4">
                {["Доктор наук", "Кандидат наук", "Магистр", "Старший преподаватель", "Ассистент"].map(
                  (filter) => (
                    <button
                      key={filter}
                      className={`w-full px-4 py-2 rounded-full border ${
                        filters.includes(filter)
                          ? "bg-green-500 text-white"
                          : "bg-gray-200"
                      }`}
                      onClick={() => toggleFilter(filter)}
                    >
                      {filter}
                    </button>
                  )
                )}
              </div>
              <button
                className="mt-4 w-full bg-gray-100 text-gray-600 py-2 rounded-md hover:bg-gray-200"
                onClick={resetFilters}
              >
                Сбросить фильтры
              </button>
            </div>
          </div>
        )}

        <ul className="w-full max-w-4xl mt-4">
          {sortedEmployees
            .filter((employee) =>
              filters.length === 0
                ? true
                : filters.includes(employee.academic_degree)
            )
            .map((employee, index) => (
              <li
                key={employee.id}
                className={`p-4 rounded-lg mb-2  ${
                  index % 2 === 0 ? "bg-white" : "bg-gray-100"
                }`}
              >
                <div className="flex items-center justify-between">
                  <Link
                    to="/employees/currentemployee"
                    state={{ employee }}
                  >
                    <div className="flex items-center cursor-pointer" >
                      <div className="flex-shrink-0 bg-gray-300 p-2 rounded-md">
                        <FaUserLarge className="text-gray-600" />
                      </div>
                      <div className="ml-4">
                        <p className="text-lg font-medium">
                          {employee.last_name} {employee.first_name}{" "}
                          {employee.middle_name}
                        </p>
                      </div>
                    </div>

                  </Link>
                  <div className="px-3">

                  <IoIosArrowDown
                    onClick={() => toggleExpand(employee.id)}
                    className={`text-xl text-gray-500 transition-transform duration-300 cursor-pointer ${
                      expanded.has(employee.id) ? "rotate-180" : "rotate-0"
                    }`}
                  />  
                  </div>
                  
                </div>
                <div
                  className={`overflow-hidden transition-all duration-300 ${
                    expanded.has(employee.id) ? "max-h-96" : "max-h-0"
                  }`}
                >
                  <div className="flex justify-end mt-4">
                    <Link
                      to="/employees/currentemployee"
                      state={{ employee }}
                      className="text-gray-500 underline"
                    >
                      Подробнее
                    </Link>
                  </div>
                  <div className={`${index % 2 === 0 ? "bg-white" : "bg-gray-100"} p-6 rounded-lg`}>
                    <h3 className="text-lg font-semibold text-gray-600 mb-4">Работа</h3>
                    <div className="flex">
                      <div className=" flex-1 min-w-[100px]">
                        <span className="text-xs text-gray-400 block">Учёная степень</span>
                        <p>{employee.academic_degree}</p>
                      </div>
                      <div className="flex-1 min-w-[100px]">
                        <span className="text-xs text-gray-400 block">Должность</span>
                        <p>{employee.post}</p>
                      </div>
                      <div className="flex-1 min-w-[100px]">
                        <span className="text-xs text-gray-400 block">Тип занятости</span>
                        <p>{employee.employment_type}</p>
                      </div>
                      <div className="flex-1 min-w-[100px]">
                        <span className="text-xs text-gray-400 block">Организационное подразделение</span>
                        <p>{employee.organizational_unit}</p>
                      </div>
                    </div>
                  </div>

                  <div className={`${index % 2 === 0 ? "bg-white" : "bg-gray-100"} p-6 rounded-lg`}>
                    <h3 className="text-lg font-semibold text-gray-600 mb-4">Документы</h3>
                    <div className="flex flex-wrap gap-4">
                      <div className="flex-1 min-w-[150px]">
                        <span className="text-xs text-gray-400 block">Паспортные данные</span>
                        <p>{employee.passport_info}</p>
                      </div>
                      <div className="flex-1 min-w-[150px]">
                        <span className="text-xs text-gray-400 block">СНИЛС</span>
                        <p>{employee.snils}</p>
                      </div>
                      <div className="flex-1 min-w-[150px]">
                        <span className="text-xs text-gray-400 block">ИНН</span>
                        <p>{employee.inn}</p>
                      </div>
                      <div className="flex-1 min-w-[150px]">
                        <span className="text-xs text-gray-400 block">Справка о несудимости</span>
                        <p>Дата получения: {employee.certificate_of_no_criminal_record.date_of_receipt}</p>
                        <p>Действует до: {employee.certificate_of_no_criminal_record.active_until}</p>
                      </div>
                    </div>
                  </div>
                </div>
              </li>
            ))}
        </ul>
      </div>
    </div>
  );
}
