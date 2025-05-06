import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import Navbar from "../Navbar";
import { IoIosArrowDown, IoIosClose } from "react-icons/io";
import { IoAnalyticsOutline } from "react-icons/io5";
import { FaPlus, FaUserLarge } from "react-icons/fa6";
import { ImTree } from "react-icons/im";
import { CiCirclePlus } from "react-icons/ci";
import { RiHistoryLine } from "react-icons/ri";

import {
  startProcess,
  cancelProcess,
  cancelSubstep,
  getAllExecutionStatuses,
  getEmployees,
  getTemplates,
  getEmployeeById,
  getTemplateById,
  executeCommon,
  executeSubstep,
  executeConditional,
  Employee,
} from "@/api";

import HistoryProcesses from "./HistoryProcesses"; // Import the HistoryProcesses component

interface Template {
  id: string;
  name: string;
  duration: number;
  steps: {
    id: string;
    description: string;
    type: number;
    data?: {
      subtasks?: { id: string; description: string; duration: number }[];
      next?: number;
    };
  }[];
}

interface Process {
  processId: string;
  name: string;
  statuses: {
    employeeId: string;
    stepId: string;
    deadline: string;
    type: number;
    substepStatuses?: { substepId: string; isCompleted: boolean }[];
  }[];
}

const CurrentProcesses: React.FC = () => {
  const [expanded, setExpanded] = useState<Set<string>>(new Set());
  const [expandedEmployees, setExpandedEmployees] = useState<Set<string>>(
    new Set()
  );
  const [showAddProcessModal, setShowAddProcessModal] = useState<boolean>(false);
  const [templates, setTemplates] = useState<Template[]>([]);
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [selectedTemplate, setSelectedTemplate] = useState<string>("");
  const [selectedEmployees, setSelectedEmployees] = useState<Set<string>>(
    new Set()
  );
  const [deadline, setDeadline] = useState<string>("");
  const [searchQuery, setSearchQuery] = useState<string>("");
  const [processes, setProcesses] = useState<Process[]>([]);
  const [employeeNames, setEmployeeNames] = useState<Map<string, string>>(
    new Map()
  );
  const [templateDuration, setTemplateDuration] = useState<number | null>(null);
  const [processTemplates, setProcessTemplates] = useState<
    Map<string, Template>
  >(new Map());
  const [employeeToDelete, setEmployeeToDelete] = useState<{
    employeeId: string;
    processId: string;
  } | null>(null);
  const [showHistory, setShowHistory] = useState<boolean>(false); // State for toggling history view

  const fetchProcesses = async () => {
    const processes = await getAllExecutionStatuses();
    setProcesses(processes);

    const employeeIds = new Set<string>();
    const templateIds = new Set<string>();
    processes.forEach((process) => {
      process.statuses.forEach((status) => {
        employeeIds.add(status.employeeId);
      });
      templateIds.add(process.processId);
    });

    const namesMap = new Map<string, string>();
    for (const employeeId of employeeIds) {
      const employee = await getEmployeeById(employeeId);
      namesMap.set(
        employeeId,
        `${employee.lastName} ${employee.firstName} ${employee.middleName}`
      );
    }

    const templateMap = new Map<string, Template>();
    for (const templateId of templateIds) {
      const template = await getTemplateById(templateId);
      templateMap.set(templateId, template);
    }

    setEmployeeNames(namesMap);
    setProcessTemplates(templateMap);
  };

  useEffect(() => {
    const fetchTemplates = async () => {
      const templates = await getTemplates();
      setTemplates(templates);
    };

    const fetchEmployees = async () => {
      const employees = await getEmployees();
      setEmployees(employees);
    };

    fetchTemplates();
    fetchEmployees();
    fetchProcesses();
  }, []);

  const toggleExpand = (id: string) => {
    setExpanded((prev) => {
      const newSet = new Set(prev);
      newSet.has(id) ? newSet.delete(id) : newSet.add(id);
      return newSet;
    });
  };

  const toggleEmployeeExpand = (id: string) => {
    setExpandedEmployees((prev) => {
      const newSet = new Set(prev);
      newSet.has(id) ? newSet.delete(id) : newSet.add(id);
      return newSet;
    });
  };

  const handleEmployeeSelect = (employeeId: string) => {
    setSelectedEmployees((prev) => {
      const newSet = new Set(prev);
      newSet.has(employeeId)
        ? newSet.delete(employeeId)
        : newSet.add(employeeId);
      return newSet;
    });
  };

  const handleCreateProcess = async () => {
    if (!selectedTemplate || !deadline || selectedEmployees.size === 0) {
      alert(
        "Please select a template, set a deadline, and choose at least one employee."
      );
      return;
    }

    for (const employeeId of selectedEmployees) {
      await startProcess(employeeId, selectedTemplate, deadline);
    }

    await fetchProcesses();

    setShowAddProcessModal(false);
    setSelectedTemplate("");
    setSelectedEmployees(new Set());
    setDeadline("");
    setTemplateDuration(null);
  };

  const handleTemplateChange = async (templateId: string) => {
    setSelectedTemplate(templateId);
    const template = await getTemplateById(templateId);
    setTemplateDuration(template.duration);
  };

  const handleDeleteEmployee = (employeeId: string, processId: string) => {
    setEmployeeToDelete({ employeeId, processId });
  };

  const confirmDeleteEmployee = async () => {
    if (employeeToDelete) {
      await cancelProcess(
        employeeToDelete.employeeId,
        employeeToDelete.processId
      );
      setEmployeeToDelete(null);
      await fetchProcesses();
    }
  };

  const cancelDeleteEmployee = () => {
    setEmployeeToDelete(null);
  };

  const handleSubstepChange = async (
    employeeId: string,
    processId: string,
    substepId: string,
    isChecked: boolean
  ) => {
    try {
      if (isChecked) {
        await executeSubstep(employeeId, processId, substepId);
      } else {
        await cancelSubstep(employeeId, processId, substepId);
      }
      await fetchProcesses();
    } catch (error) {
      console.error("Error executing/canceling substep:", error);
      alert(
        "Произошла ошибка при выполнении/отмене подшага. Пожалуйста, попробуйте позже."
      );
    }
  };

  const filteredEmployees = employees.filter(
    (employee) =>
      employee.firstName &&
      employee.lastName &&
      (employee.firstName.toLowerCase().includes(searchQuery.toLowerCase()) ||
        employee.lastName.toLowerCase().includes(searchQuery.toLowerCase()) ||
        (employee.middleName &&
          employee.middleName
            .toLowerCase()
            .includes(searchQuery.toLowerCase())))
  );

  const getDeadlineMessage = () => {
    if (!deadline) {
      return `Дедлайн шаблона: ${new Date(
        Date.now() + templateDuration! * 24 * 60 * 60 * 1000
      ).toLocaleDateString()} (рекомендуемая дата)`;
    }

    const deadlineDate = new Date(deadline);
    const recommendedDate = new Date(
      Date.now() + templateDuration! * 24 * 60 * 60 * 1000
    );

    if (deadlineDate.getTime() > recommendedDate.getTime()) {
      const daysDifference = Math.ceil(
        (deadlineDate.getTime() - recommendedDate.getTime()) /
          (1000 * 60 * 60 * 24)
      );
      return `Дедлайн шаблона: ${recommendedDate.toLocaleDateString()}, дней в запасе: ${daysDifference}`;
    } else if (deadlineDate.getTime() === recommendedDate.getTime()) {
      return `Дедлайн шаблона: ${recommendedDate.toLocaleDateString()}, точно как вы указали`;
    } else {
      return `Дедлайн шаблона: ${recommendedDate.toLocaleDateString()}, вы можете не успеть выполнить все задачи`;
    }
  };

  const sortedProcesses = () => {
    return processes.sort((a, b) => {
      const closestDeadlineA = Math.min(
        ...a.statuses.map((status) => new Date(status.deadline).getTime())
      );
      const closestDeadlineB = Math.min(
        ...b.statuses.map((status) => new Date(status.deadline).getTime())
      );
      return closestDeadlineA - closestDeadlineB;
    });
  };

  const getClosestDeadline = (process: Process) => {
    const closestDeadline = Math.min(
      ...process.statuses.map((status) => new Date(status.deadline).getTime())
    );
    return new Date(closestDeadline).toLocaleDateString();
  };

  return (
    <div>
      <Navbar />
      <div className="flex flex-col items-center p-4">
        <div className="w-full max-w-4xl bg-white border border-gray-300 p-4 rounded-lg sticky top-24 flex items-center z-10 space-x-4">
          <button
            className="flex items-center space-x-2 bg-[#4fff9e] text-gray-700 px-4 py-2 rounded-md hover:bg-green-400"
            onClick={() => setShowAddProcessModal(true)}
          >
            <FaPlus className="text-lg" />
            <span>Добавить процесс</span>
          </button>
          <Link to="/processes/createprocess">
            <button className="flex items-center space-x-2 bg-[#4fe8ff] text-gray-700 px-4 py-2 rounded-md hover:bg-[#44c9de]">
              <ImTree className="text-lg" />
              <span>Шаблоны</span>
            </button>
          </Link>
          <button
            className="flex items-center space-x-2 bg-[#ffa84f] text-gray-700 px-4 py-2 rounded-md hover:bg-[#ff962d]"
            onClick={() => setShowHistory(!showHistory)}
          >
            <RiHistoryLine className="text-lg" />
            <span>История</span>
          </button>
        </div>

        {showAddProcessModal && (
          <div className="fixed inset-0 z-50 bg-gray-900 bg-opacity-50 flex justify-center items-center">
            <div className="bg-white w-96 p-6 rounded-lg relative">
              <button
                className="absolute top-2 right-2 text-gray-500 hover:text-gray-700"
                onClick={() => setShowAddProcessModal(false)}
              >
                <IoIosClose className="text-2xl" />
              </button>
              <h3 className="text-lg font-semibold mb-4">Добавить процесс</h3>
              <div className="mb-4">
                <label className="block mb-2 text-gray-500">
                  Выберите шаблон:
                </label>
                <select
                  className="w-full px-4 py-2 border rounded-md"
                  value={selectedTemplate}
                  onChange={(e) => handleTemplateChange(e.target.value)}
                >
                  <option value="" disabled>
                    Выберите шаблон
                  </option>
                  {templates.map((template) => (
                    <option key={template.id} value={template.id}>
                      {template.name}
                    </option>
                  ))}
                </select>
              </div>
              <div className="mb-4">
                <label className="block mb-2 text-gray-500">
                  Выберите сотрудников:
                </label>
                <input
                  type="text"
                  className="w-full px-4 py-2 border rounded-md mb-2"
                  placeholder="Поиск сотрудников"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                />
                <div className="max-h-40 overflow-y-auto border rounded-md">
                  <ul>
                    {filteredEmployees.map((employee) => (
                      <li
                        key={employee.id}
                        className={`px-4 py-2 border-b cursor-pointer ${
                          selectedEmployees.has(employee.id)
                            ? "bg-green-200"
                            : ""
                        }`}
                        onClick={() => handleEmployeeSelect(employee.id)}
                      >
                        {`${employee.lastName} ${employee.firstName} ${employee?.middleName}`}
                      </li>
                    ))}
                  </ul>
                </div>
              </div>
              <div className="mb-4">
                <label className="block mb-2 text-gray-500">Дедлайн:</label>
                <input
                  type="date"
                  className="w-full px-4 py-2 border rounded-md"
                  value={deadline}
                  onChange={(e) => setDeadline(e.target.value)}
                />
                {templateDuration !== null && (
                  <p
                    className={`mt-2 text-sm ${
                      deadline
                        ? new Date(deadline).getTime() >=
                          new Date(
                            Date.now() + templateDuration * 24 * 60 * 60 * 1000
                          ).getTime()
                          ? "text-green-500"
                          : "text-red-500"
                        : "text-red-500"
                    }`}
                  >
                    {getDeadlineMessage()}
                  </p>
                )}
              </div>
              <button
                className="w-full bg-green-500 text-white py-2 rounded-md hover:bg-green-600"
                onClick={handleCreateProcess}
              >
                Создать процесс
              </button>
            </div>
          </div>
        )}

        <div className="w-full max-w-4xl mt-4">
          {showHistory ? (
            <HistoryProcesses />
          ) : (
            <>
              {processes.length === 0 ? (
                <div className="flex flex-col items-center justify-center h-full text-gray-500">
                  <CiCirclePlus className="text-6xl mb-4" />
                  <p>
                    Нет текущих процессов, добавьте с помощью "Добавить процесс"
                  </p>
                </div>
              ) : (
                <ul>
                  {sortedProcesses().map((process, index) => (
                    <li
                      key={process.processId}
                      className={`p-3 rounded-lg mb-2 ${
                        index % 2 === 0 ? "bg-white" : "bg-gray-100"
                      }`}
                    >
                      <div
                        className="flex cursor-pointer items-center justify-between"
                        onClick={() => toggleExpand(process.processId)}
                      >
                        <div className="flex items-center">
                          <div className="flex-shrink-0 bg-gray-300 p-2 rounded-md">
                            <IoAnalyticsOutline className="text-gray-600" />
                          </div>
                          <div className="ml-4">
                            <p className="text-lg font-medium">{process.name}</p>
                          </div>
                        </div>
                        <div className="flex items-center">
                          <div className="mr-3 flex items-center">
                            <p className="text-sm mr-2 border-r pr-2">
                              Ближайший дедлайн: {getClosestDeadline(process)}
                            </p>
                            <p className="text-sm">
                              Участников: {process.statuses.length}
                            </p>
                          </div>
                          <div className="px-3 rounded-md flex items-center">
                            <IoIosArrowDown
                              className={`text-xl text-gray-500 transition-transform duration-300 ${
                                expanded.has(process.processId)
                                  ? "rotate-180"
                                  : "rotate-0"
                              }`}
                            />
                          </div>
                        </div>
                      </div>

                      {expanded.has(process.processId) && (
                        <ul className="mt-2">
                          {process.statuses.map((status) => {
                            const template = processTemplates.get(
                              process.processId
                            );
                            const step = template?.steps.find(
                              (s) => s.id === parseInt(status.stepId)
                            );
                            const stepDescription = step?.description;
                            const subtasks = step?.data?.subtasks;

                            return (
                              <li
                                key={`${process.processId}-${status.employeeId}`}
                                className="flex flex-col items-start ml-12 py-2 border-b relative"
                              >
                                <div className="flex items-center w-full justify-between">
                                  <div className="flex items-center w-full">
                                    <div className="bg-gray-300 p-[5px] rounded-md mr-2">
                                      <FaUserLarge className="text-gray-600 text-[10px]" />
                                    </div>
                                    <div className="flex-1 flex items-center">
                                      {employeeNames.get(status.employeeId)}
                                      <span className="ml-2 text-gray-500">
                                        Шаг:{" "}
                                        {stepDescription?.length > 50
                                          ? `${stepDescription.substring(0, 30)}...`
                                          : stepDescription}
                                      </span>
                                    </div>
                                    <p className="ml-auto text-gray-500">
                                      Дедлайн:{" "}
                                      {new Date(
                                        status.deadline
                                      ).toLocaleDateString()}
                                    </p>
                                  </div>

                                  <div className="px-3 cursor-pointer rounded-md flex items-center">
                                    <IoIosArrowDown
                                      className={`text-xl text-gray-500 transition-transform duration-300 ${
                                        expandedEmployees.has(status.employeeId)
                                          ? "rotate-180"
                                          : "rotate-0"
                                      }`}
                                      onClick={() =>
                                        toggleEmployeeExpand(status.employeeId)
                                      }
                                    />
                                  </div>
                                </div>
                                {expandedEmployees.has(status.employeeId) && (
                                  <div className="mt-2 ml-6 flex flex-col space-y-2">
                                    {status.type === 1 && (
                                      <>
                                        <p className="font-semibold mb-2">
                                          Шаг: {stepDescription}
                                        </p>
                                        <button
                                          className="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600"
                                          onClick={async () => {
                                            await executeCommon(
                                              status.employeeId,
                                              process.processId,
                                              process.processId,
                                              parseInt(status.stepId)
                                            );
                                            await fetchProcesses(); // Refresh after executing common step
                                          }}
                                        >
                                          Выполнено
                                        </button>
                                      </>
                                    )}
                                    {status.type === 2 && (
                                      <>
                                        <p className="font-semibold mb-2">
                                          Шаг: {stepDescription}
                                        </p>
                                        <div className="mb-4">
                                          {subtasks?.map((subtask) => {
                                            const isCompleted =
                                              status.substepStatuses?.find(
                                                (s) => s.substepId === subtask.id
                                              )?.isCompleted || false;
                                            return (
                                              <div
                                                key={subtask.id}
                                                className="flex items-center mb-2"
                                              >
                                                <input
                                                  type="checkbox"
                                                  className="mr-2"
                                                  checked={isCompleted}
                                                  onChange={(e) =>
                                                    handleSubstepChange(
                                                      status.employeeId,
                                                      process.processId,
                                                      subtask.id,
                                                      e.target.checked
                                                    )
                                                  }
                                                />
                                                <span>{subtask.description}</span>
                                              </div>
                                            );
                                          })}
                                        </div>
                                      </>
                                    )}
                                    {status.type === 3 && (
                                      <>
                                        <p className="font-semibold mb-2">
                                          Шаг: {stepDescription}
                                        </p>
                                        <div className="flex space-x-4">
                                          <button
                                            className="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600"
                                            onClick={async () => {
                                              await executeConditional(
                                                status.employeeId,
                                                process.processId,
                                                process.processId,
                                                parseInt(status.stepId),
                                                true
                                              );
                                              await fetchProcesses();
                                            }}
                                          >
                                            Да
                                          </button>
                                          <button
                                            className="bg-red-500 text-white px-4 py-2 rounded hover:bg-red-600"
                                            onClick={async () => {
                                              await executeConditional(
                                                status.employeeId,
                                                process.processId,
                                                process.processId,
                                                parseInt(status.stepId),
                                                false
                                              );
                                              await fetchProcesses();
                                            }}
                                          >
                                            Нет
                                          </button>
                                        </div>
                                      </>
                                    )}

                                    <button
                                      className="text-red-500"
                                      onClick={() =>
                                        handleDeleteEmployee(
                                          status.employeeId,
                                          process.processId
                                        )
                                      }
                                    >
                                      Удалить
                                    </button>
                                  </div>
                                )}
                              </li>
                            );
                          })}
                        </ul>
                      )}
                    </li>
                  ))}
                </ul>
              )}
            </>
          )}
        </div>
      </div>

      {employeeToDelete && (
        <div className="fixed inset-0 z-50 bg-gray-900 bg-opacity-50 flex justify-center items-center">
          <div className="bg-white p-6 rounded-lg">
            <p className="mb-4">
              Вы действительно хотите удалить сотрудника из процесса?
            </p>
            <div className="flex justify-end">
              <button
                className="mr-2 px-4 py-2 bg-gray-300 rounded"
                onClick={cancelDeleteEmployee}
              >
                Нет
              </button>
              <button
                className="px-4 py-2 bg-red-500 text-white rounded"
                onClick={confirmDeleteEmployee}
              >
                Да
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default CurrentProcesses;
