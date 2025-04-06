import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import Navbar from "../Navbar";
import { FaUserLarge } from "react-icons/fa6";
import { IoIosArrowDown, IoIosClose } from "react-icons/io";
import { HiArrowsUpDown } from "react-icons/hi2";
import { VscSettings } from "react-icons/vsc";
import { getEmployeesInfo, createEmployee, Employee } from "@/api";
import { FaPlus } from "react-icons/fa6";

export default function EmployeesPage() {
  const [sortOrder, setSortOrder] = useState<string>("По фамилии ↑");
  const [filters, setFilters] = useState<string[]>([]);
  const [expanded, setExpanded] = useState<Set<string>>(new Set());
  const [showFilters, setShowFilters] = useState<boolean>(false);
  const [isSortDropdownVisible, setIsSortDropdownVisible] = useState<boolean>(false);
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [showAddEmployeeDialog, setShowAddEmployeeDialog] = useState<boolean>(false);
  const [newEmployee, setNewEmployee] = useState<{
    firstName: string;
    lastName: string;
    middleName?: string;
  }>({
    firstName: "",
    lastName: "",
    middleName: "",
  });
  const [formError, setFormError] = useState<string | null>(null);

  useEffect(() => {
    const fetchEmployees = async () => {
      try {
        const data = await getEmployeesInfo();
        setEmployees(data);
      } catch (error) {
        if (error instanceof Error) {
          setError(error.message);
        } else {
          setError("An unknown error occurred.");
        }
      } finally {
        setLoading(false);
      }
    };

    fetchEmployees();
  }, []);

  const toggleFilter = (filter: string) => {
    setFilters((prevFilters) =>
      prevFilters.includes(filter)
        ? prevFilters.filter((item) => item !== filter)
        : [...prevFilters, filter]
    );
  };

  const resetFilters = () => setFilters([]);

  const toggleExpand = (id: string) => {
    setExpanded((prev) => {
      const newSet = new Set(prev);
      newSet.has(id) ? newSet.delete(id) : newSet.add(id);
      return newSet;
    });
  };

  const sortedEmployees = [...employees];
  if (sortOrder === "По фамилии ↑") {
    sortedEmployees.sort((a, b) => a.lastName.localeCompare(b.lastName));
  } else if (sortOrder === "По фамилии ↓") {
    sortedEmployees.sort((a, b) => b.lastName.localeCompare(a.lastName));
  }

  const filteredEmployees = sortedEmployees.filter((employee) =>
    filters.length === 0 ? true : filters.includes(employee.academicDegree.name)
  );

  const handleAddEmployee = async () => {
    if (!newEmployee.firstName || !newEmployee.lastName) {
      setFormError("Необходимо указать фамилию и имя");
      return;
    }

    const employeeData = {
      ...newEmployee,
      agreement: false,
      hasCompletedAdvancedCourses: false,
      hasHigherEducation: false,
      needsMandatoryElection: false,
      snils: null,
      inn: null,
      isArchived: false,
    };

    try {
      const createdEmployee = await createEmployee(employeeData);
      setEmployees([...employees, createdEmployee]);
      setShowAddEmployeeDialog(false);
      setNewEmployee({ firstName: "", lastName: "", middleName: "" });
      setFormError(null);
    } catch (error) {
      console.error("Error creating employee:", error);
    }
  };

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
            <button className="flex items-center px-4 py-2 bg-gray-200 rounded-md">
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
          <button
            className="ml-4 flex items-center space-x-2 bg-[#4fff9e] text-gray-700 px-4 py-2 rounded-md hover:bg-green-400"
            onClick={() => setShowAddEmployeeDialog(true)}
          >
            <FaPlus className="text-lg" />
            <span>Добавить сотрудника</span>
          </button>
        </div>

        {showFilters && (
          <div className="fixed inset-0 z-50 bg-gray-900 bg-opacity-50 flex justify-center items-center">
            <div className="bg-white w-96 p-6 rounded-lg relative">
              <button
                className="absolute top-2 right-2 text-gray-500 hover:text-gray-700"
                onClick={() => setShowFilters(false)}
              >
                <IoIosClose className="text-2xl" />
              </button>
              <h3 className="text-lg font-semibold mb-4">Фильтры</h3>
              <div className="space-y-4">
                {[
                  "Доктор наук",
                  "Кандидат наук",
                  "Магистр",
                  "Старший преподаватель",
                  "Ассистент",
                ].map((filter) => (
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
                ))}
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

        {showAddEmployeeDialog && (
          <div className="fixed inset-0 z-50 bg-gray-900 bg-opacity-50 flex justify-center items-center">
            <div className="bg-white w-96 p-6 rounded-lg relative">
              <button
                className="absolute top-2 right-2 text-gray-500 hover:text-gray-700"
                onClick={() => setShowAddEmployeeDialog(false)}
              >
                <IoIosClose className="text-2xl" />
              </button>
              <h3 className="text-lg font-semibold mb-4">Добавить сотрудника</h3>
              <div className="space-y-4">
                <input
                  type="text"
                  placeholder="Имя"
                  value={newEmployee.firstName}
                  onChange={(e) =>
                    setNewEmployee({ ...newEmployee, firstName: e.target.value })
                  }
                  className="w-full px-4 py-2 border rounded-md"
                />
                <input
                  type="text"
                  placeholder="Фамилия"
                  value={newEmployee.lastName}
                  onChange={(e) =>
                    setNewEmployee({ ...newEmployee, lastName: e.target.value })
                  }
                  className="w-full px-4 py-2 border rounded-md"
                />
                <input
                  type="text"
                  placeholder="Отчество (необязательно)"
                  value={newEmployee.middleName || ""}
                  onChange={(e) =>
                    setNewEmployee({ ...newEmployee, middleName: e.target.value })
                  }
                  className="w-full px-4 py-2 border rounded-md"
                />
              </div>
              {formError && <p className="text-red-500">{formError}</p>}
              <button
                className="mt-4 w-full bg-green-500 text-white py-2 rounded-md hover:bg-green-600"
                onClick={handleAddEmployee}
              >
                Добавить
              </button>
            </div>
          </div>
        )}

        <div className="w-full max-w-4xl mt-4">
          {loading && <div>Loading...</div>}
          {!loading && filteredEmployees.length === 0 && (
            <div>
              Нет сотрудников в списке, добавьте с помощью "Добавить
              сотрудника".
            </div>
          )}
          {!loading && filteredEmployees.length > 0 && (
            <ul>
              {filteredEmployees.map((employee, index) => (
                <li
                  key={employee.id}
                  className={`p-2 rounded-lg mb-2  ${
                    index % 2 === 0 ? "bg-white" : "bg-gray-100"
                  }`}
                >
                  <div className="flex items-center justify-between">
                    <Link to={`/employees/currentemployee?id=${employee.id}`} state={{ employee }}>
                      <div className="flex items-center cursor-pointer">
                        <div className="flex-shrink-0 bg-gray-300 p-2 rounded-md">
                          <FaUserLarge className="text-gray-600" />
                        </div>
                        <div className="ml-4">
                          <p className="text-lg font-medium">
                            {employee.lastName} {employee.firstName}{" "}
                            {employee.middleName}
                          </p>
                        </div>
                      </div>
                    </Link>
                    <div className="px-3">
                      <IoIosArrowDown
                        onClick={() => toggleExpand(employee.id)}
                        className={`text-xl text-gray-500 transition-transform duration-300 cursor-pointer ${
                          expanded.has(employee.id)
                            ? "rotate-180"
                            : "rotate-0"
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
                        to={`/employees/employeeform?id=${employee.id}`}
                        state={{ employee }}
                        className="text-gray-500 underline"
                      >
                        Редактировать
                      </Link>
                      <Link
                        to={`/employees/currentemployee?id=${employee.id}`}
                        state={{ employee }}
                        className="text-gray-500 underline ml-4"
                      >
                        Подробнее 
                      </Link>
                    </div>
                    <div
                      className={`${
                        index % 2 === 0 ? "bg-white" : "bg-gray-100"
                      } p-6 rounded-lg`}
                    >
                      <h3 className="text-lg font-semibold text-gray-600 mb-4">
                        Работа
                      </h3>
                      <div className="flex">
                        <div className=" flex-1 min-w-[100px]">
                          <span className="text-xs text-gray-400 block">
                            Учёная степень
                          </span>
                          <p>{employee?.academicDegree?.name}</p>
                        </div>
                        <div className="flex-1 min-w-[100px]">
                          <span className="text-xs text-gray-400 block">
                            Должность
                          </span>
                          <p>{employee.post}</p>
                        </div>
                        <div className="flex-1 min-w-[100px]">
                          <span className="text-xs text-gray-400 block">
                            Тип занятости
                          </span>
                          <p>{employee?.employmentStatus?.employmentInfo}</p>
                        </div>
                        <div className="flex-1 min-w-[100px]">
                          <span className="text-xs text-gray-400 block">
                            Организационное подразделение
                          </span>
                          <p>{employee.organizational_unit}</p>
                        </div>
                      </div>
                    </div>

                    <div
                      className={`${
                        index % 2 === 0 ? "bg-white" : "bg-gray-100"
                      } p-6 rounded-lg`}
                    >
                      <h3 className="text-lg font-semibold text-gray-600 mb-4">
                        Документы
                      </h3>
                      <div className="flex flex-wrap gap-4">
                        <div className="flex-1 min-w-[150px]">
                          <span className="text-xs text-gray-400 block">
                            Паспортные данные
                          </span>
                          <p>{employee?.passportInfo?.passportInfo}</p>
                        </div>
                        <div className="flex-1 min-w-[150px]">
                          <span className="text-xs text-gray-400 block">
                            СНИЛС
                          </span>
                          <p>{employee.snils}</p>
                        </div>
                        <div className="flex-1 min-w-[150px]">
                          <span className="text-xs text-gray-400 block">
                            ИНН
                          </span>
                          <p>{employee.inn}</p>
                        </div>
                        <div className="flex-1 min-w-[150px]">
                          <span className="text-xs text-gray-400 block">
                            Справка о несудимости
                          </span>
                          <p>
                            Дата получения:{" "}
                            {
                              employee.certificateOfNoCriminalRecord?.dateOfReceipt
                            }
                          </p>
                          <p>
                            Действует до:{" "}
                            {
                              employee.certificateOfNoCriminalRecord
                                ?.expirationDate
                            }
                          </p>
                        </div>
                      </div>
                    </div>
                  </div>
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>
    </div>
  );
}
