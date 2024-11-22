// TODO: под контактами процессы, в которых участвует сотрудник


import { useState } from "react";
import Navbar from "../Navbar";
import { Link, useLocation } from "react-router-dom";
import { FaUserLarge } from "react-icons/fa6";
import { FaArrowLeft} from "react-icons/fa";
import { MdContentCopy } from "react-icons/md";

export default function CurrentEmployeePage() {
  const location = useLocation();
  const employee = location.state?.employee;
  const [copied, setCopied] = useState<"phone" | "email" | null>(null);

  if (!employee) return <div>Employee data not found.</div>;


  const handleCopy = (text: string, type: "phone" | "email") => {
    navigator.clipboard.writeText(text)
      .then(() => {
        setCopied(type);
        setTimeout(() => setCopied(null), 2000); 
      })
      .catch(() => {
        alert("Failed to copy text.");
      });
  };

  return (
    <div>
      <Navbar />

      <div className="p-4">
      <div className=" bg-gray-200 p-4 rounded-lg mb-4">
        <Link to="/employees">
            <FaArrowLeft/>   
        </Link>
      </div>
        <div className="flex justify-between">
        <div className="w-1/3 flex flex-col items-center bg-gray-100 p-4 rounded-lg">
            <div className="bg-gray-300 p-6 rounded-full mb-4">
              <FaUserLarge className="text-gray-600 text-6xl" />
            </div>
            <p className="text-xl font-bold">
              {employee.last_name} {employee.first_name} {employee.middle_name}
            </p>
            <div className="mt-4 w-full bg-gray-200 p-4 rounded-lg">
              <h3 className="text-lg font-semibold text-gray-600 mb-2">Контакты</h3>
              <div className="flex justify-between items-center">
                <p className="text-sm text-gray-500">Телефон: {employee.phone_number}</p>
                <MdContentCopy 
                  className="cursor-pointer text-gray-500" 
                  onClick={() => handleCopy(employee.phone_number, "phone")} 
                />
              </div>
              <div className="flex justify-between items-center mt-2">
                <p className="text-sm text-gray-500">E-mail: {employee.email}</p>
                <MdContentCopy 
                  className="cursor-pointer text-gray-500" 
                  onClick={() => handleCopy(employee.email, "email")} 
                />
              </div>
              {copied && (
                <p className="text-xs text-green-500 mt-2">
                  Copied to clipboard
                </p>
              )}
            </div>
            <div className="mt-4 w-full bg-gray-200 p-4 rounded-lg">
              <h3 className="text-lg font-semibold text-gray-600 mb-2">Процессы</h3>
              <ol>
                <li className="mt-4 w-full bg-gray-300 p-2 rounded-lg">1</li>
                <li className="mt-4 w-full bg-gray-300 p-2 rounded-lg">1</li>
                <li className="mt-4 w-full bg-gray-300 p-2 rounded-lg">1</li>
                <li className="mt-4 w-full bg-gray-300 p-2 rounded-lg">1</li>
                <li className="mt-4 w-full bg-gray-300 p-2 rounded-lg">1</li>
                <li className="mt-4 w-full bg-gray-300 p-2 rounded-lg">1</li>
                <li className="mt-4 w-full bg-gray-300 p-2 rounded-lg">1</li>
                <li className="mt-4 w-full bg-gray-300 p-2 rounded-lg">1</li>
                <li className="mt-4 w-full bg-gray-300 p-2 rounded-lg">1</li>
                <li className="mt-4 w-full bg-gray-300 p-2 rounded-lg">1</li>
                <li className="mt-4 w-full bg-gray-300 p-2 rounded-lg">1</li>
                <li className="mt-4 w-full bg-gray-300 p-2 rounded-lg">1</li>
                <li className="mt-4 w-full bg-gray-300 p-2 rounded-lg">1</li>
              </ol>
            </div>
          </div>

          <div className="w-2/3 ml-6 space-y-6">
            <div className="bg-gray-100 p-6 rounded-lg">
              <h3 className="text-lg font-semibold text-gray-600 mb-4">Работа</h3>
              <div className="mt-3">
                <span className="text-xs text-gray-400 block">Учёная степень</span>
                <p>{employee.academic_degree}</p>
              </div>
              <div className="mt-3">
                <span className="text-xs text-gray-400 block">Должность</span>
                <p>{employee.post}</p>
              </div>
              <div className="mt-3">
                <span className="text-xs text-gray-400 block">Тип занятости</span>
                <p>{employee.employment_type}</p>
              </div>
              <div className="mt-3">
                <span className="text-xs text-gray-400 block">Организационное подразделение</span>
                <p>{employee.organizational_unit}</p>
              </div>
            </div>

            <div className=" bg-gray-100 p-6 rounded-lg">
              <h3 className="text-lg font-semibold text-gray-600 mb-4">Документы</h3>
              <div className="mt-3">
                <span className="text-xs text-gray-400 block ">Паспортные данные</span>
                <p>{employee.passport_info}</p>
              </div>
              <div className="mt-3">
                <span className="text-xs text-gray-400 block">СНИЛС</span>
                <p>{employee.snils}</p>
              </div>
              <div className="mt-3">
                <span className="text-xs text-gray-400 block">ИНН</span>
                <p>{employee.inn}</p>
              </div>
              <div className="mt-3">
                <span className="text-xs text-gray-400 block">Справка о несудимости</span>
                <p>Дата получения: {employee.certificate_of_no_criminal_record.date_of_receipt}</p>
                <p>Действует до: {employee.certificate_of_no_criminal_record.active_until}</p>
              </div>
            </div>

            <div className="bg-gray-100 p-6 rounded-lg  ">
              <h3 className="text-lg font-semibold text-gray-600 mb-4">Дополнительно</h3>
              <div className="mt-3">
                <span className="text-xs text-gray-400 block">Есть ли высшее образование</span>
                <p>{employee.has_a_higher_education ? "Да" : "Нет"}</p>
              </div>
              <div className="mt-3">
                <span className="text-xs text-gray-400 block">Дата поступления</span>
                <p>{employee.date_of_receipt}</p>
              </div>
              <div className="mt-3">
                <span className="text-xs text-gray-400 block">Стаж работы (дней)</span>
                <p>{employee.work_experience_days}</p>
              </div>
              <div className="mt-3">
                <span className="text-xs text-gray-400 block">Есть ли соглашение</span>
                <p>{employee.agreement ? "Да" : "Нет"}</p>
              </div>
              <div className="mt-3">
                <span className="text-xs text-gray-400 block">Прошёл ли курсы повышения квалификации</span>
                <p>{employee.has_completed_advanced_courses ? "Да" : "Нет"}</p>
              </div>
              <div className="mt-3">
                <span className="text-xs text-gray-400 block">Нуждается ли в обязательных выборах</span>
                <p>{employee.needs_mandatory_election ? "Да" : "Нет"}</p>
              </div>
              <div className="mt-3">
                <span className="text-xs text-gray-400 block">Дополнительная информация</span>
                <p>{employee.additional_info}</p>
              </div>
              <div className="mt-3">
                <span className="text-xs text-gray-400 block">Архивировано</span>
                <p>{employee.is_archived ? "Да" : "Нет"}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
