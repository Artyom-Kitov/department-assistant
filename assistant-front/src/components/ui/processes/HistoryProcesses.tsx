import React, { useState, useEffect } from "react";
import { FiTrash2 } from "react-icons/fi";
import { deleteHistory, getExecutionHistory, getEmployeeById, getTemplateById } from "@/api";

interface HistoryProcess {
  id: string;
  employeeId: string;
  processId: string;
  startedAt: string;
  completedAt: string;
  result: string;
  isSuccessful: boolean;
}

interface Employee {
  id: string;
  firstName: string;
  lastName: string;
  middleName?: string;
}

interface Template {
  id: string;
  name: string;
}

const HistoryProcesses: React.FC = () => {
  const [history, setHistory] = useState<HistoryProcess[]>([]);
  const [employeeNames, setEmployeeNames] = useState<Map<string, string>>(new Map());
  const [processNames, setProcessNames] = useState<Map<string, string>>(new Map());
  const [showConfirmation, setShowConfirmation] = useState<boolean>(false);

  const fetchHistory = async () => {
    const historyData = await getExecutionHistory(0, 100, "startedAt", true);
    setHistory(historyData);

    const employeeIds = new Set<string>();
    const processIds = new Set<string>();
    historyData.forEach((process) => {
      employeeIds.add(process.employeeId);
      processIds.add(process.processId);
    });

    const namesMap = new Map<string, string>();
    for (const employeeId of employeeIds) {
      const employee = await getEmployeeById(employeeId);
      namesMap.set(
        employeeId,
        `${employee.lastName} ${employee.firstName} ${employee.middleName || ''}`
      );
    }
    setEmployeeNames(namesMap);

    const processMap = new Map<string, string>();
    for (const processId of processIds) {
      const template = await getTemplateById(processId);
      processMap.set(processId, template.name);
    }
    setProcessNames(processMap);
  };

  useEffect(() => {
    fetchHistory();
  }, []);

  const handleClearHistory = async () => {
    const historyIds = history.map((process) => process.id);
    await deleteHistory(historyIds);
    setHistory([]);
    setShowConfirmation(false);
  };

  return (
    <div className="p-4">
      <div className="flex justify-end mb-4">
        <button
          className="flex items-center space-x-2 bg-red-500 text-white px-4 py-2 rounded-md hover:bg-red-600"
          onClick={() => setShowConfirmation(true)}
        >
          <FiTrash2 className="text-lg" />
          <span>Очистить историю</span>
        </button>
      </div>
      <ul>
        {history.map((process) => (
          <li
            key={process.id}
            className="p-3 rounded-lg mb-2 bg-white border border-gray-300"
          >
            <p className="text-lg font-medium">Process Name: {processNames.get(process.processId)}</p>
            <p>Сотрудник: {employeeNames.get(process.employeeId)}</p>
            <p>Время начала: {new Date(process.startedAt).toLocaleDateString()}</p>
            <p>Время завершения: {new Date(process.completedAt).toLocaleDateString()}</p>
            <p>Итог: {process.result}</p>
          </li>
        ))}
      </ul>

      {showConfirmation && (
        <div className="fixed inset-0 z-50 bg-gray-900 bg-opacity-50 flex justify-center items-center">
          <div className="bg-white p-6 rounded-lg">
            <p className="mb-4">Вы уверены, что хотите очистить историю?</p>
            <div className="flex justify-end">
              <button
                className="mr-2 px-4 py-2 bg-gray-300 rounded"
                onClick={() => setShowConfirmation(false)}
              >
                Нет
              </button>
              <button
                className="px-4 py-2 bg-red-500 text-white rounded"
                onClick={handleClearHistory}
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

export default HistoryProcesses;
