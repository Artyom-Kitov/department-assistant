import { useState } from "react";
import { Link } from "react-router-dom";
import Navbar from "../Navbar";
import { IoAnalyticsOutline } from "react-icons/io5";
import { IoIosArrowDown } from "react-icons/io";
import { HiArrowsUpDown } from "react-icons/hi2";
import { VscSettings } from "react-icons/vsc";
import { FaPlus } from "react-icons/fa6";

const CurrentProcesses: React.FC = () => {
  const [sortOrder, setSortOrder] = useState<string>("По названию ↑");
  const [showFilters, setShowFilters] = useState<boolean>(false);
  const [isSortDropdownVisible, setIsSortDropdownVisible] = useState<boolean>(false);
  const [expanded, setExpanded] = useState<Set<number>>(new Set());

  const toggleExpand = (id: number) => {
    setExpanded((prev) => {
      const newSet = new Set(prev);
      newSet.has(id) ? newSet.delete(id) : newSet.add(id);
      return newSet;
    });
  };

  return (
    <div>
      <Navbar />

      <div className="flex flex-col items-center p-4">
        <div className="w-full max-w-4xl bg-white border border-gray-300 p-4 rounded-lg sticky top-24 z-10 flex items-center">
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
              <ul className="absolute right-0 bg-white border border-gray-300 rounded-md">
                {["По названию ↑", "По названию ↓"].map((option) => (
                  <li
                    key={option}
                    className="px-4 py-2 hover:bg-gray-100 cursor-pointer"
                    onClick={() => setSortOrder(option)}
                  >
                    {option}
                  </li>
                ))}
              </ul>
            )}
          </div>

          <button
            className="mr-4 flex items-center space-x-2 bg-gray-200 px-4 py-2 rounded-md hover:bg-gray-300"
            onClick={() => setShowFilters(true)}
          >
            <VscSettings className="text-lg text-gray-700" />
            <span>Фильтры</span>
          </button>
          <Link to="/processes/createprocess">
            <button className="flex items-center  space-x-2 bg-[#4fff9e] text-gray-700 px-4 py-2 rounded-md hover:bg-green-600">
              <FaPlus className="text-lg" />
              <span>Создать процесс</span>
            </button>
          </Link>
        </div>

        {showFilters && (
          <div className="fixed inset-0 z-50 bg-gray-900 bg-opacity-50 flex justify-center items-center">
            <div className="bg-white w-96 p-6 rounded-lg relative">
              <button
                className="absolute top-2 right-2 text-gray-500 hover:text-gray-700"
                onClick={() => setShowFilters(false)}
              >
                ✕
              </button>
              <h3 className="text-lg font-semibold mb-4">Фильтры</h3>
              <div className="space-y-4">
                {["Активные", "Завершённые", "Ошибка"].map((filter) => (
                  <button
                    key={filter}
                    className="w-full px-4 py-2 rounded-full border bg-gray-200"
                  >
                    {filter}
                  </button>
                ))}
              </div>
              <button className="mt-4 w-full bg-gray-100 text-gray-600 py-2 rounded-md hover:bg-gray-200">
                Сбросить фильтры
              </button>
            </div>
          </div>
        )}

        <ul className="w-full max-w-4xl mt-4">
          {Array.from({ length: 10 }, (_, index) => (
            <li
              key={index}
              className={`p-4 rounded-lg mb-2 ${
                index % 2 === 0 ? "bg-white" : "bg-gray-100"
              }`}
            >
              <div className="flex items-center justify-between">
                <div className="flex items-center">
                  <div className="flex-shrink-0 bg-gray-300 p-2 rounded-md">
                    <IoAnalyticsOutline className="text-gray-600" />
                  </div>
                  <div className="ml-4">
                    <p className="text-lg font-medium">Процесс {index + 1}</p>
                  </div>
                </div>
                <div
                  className="px-3 cursor-pointer rounded-md "
                  onClick={() => toggleExpand(index)}
                >
                  <IoIosArrowDown
                    className={`text-xl text-gray-500 transition-transform duration-300 ${
                      expanded.has(index) ? "rotate-180" : "rotate-0"
                    }`}
                  />
                </div>
              </div>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
};

export default CurrentProcesses;
