import { Handle, Position } from "@xyflow/react";
import { useState } from "react";
import { TiDeleteOutline } from "react-icons/ti";
import "./no-spinners.css";

interface CheckBlockNodeProps {
  data: {
    description?: string;
    checks?: { description: string; days: string }[];
  };
}

const CheckBlockNode: React.FC<CheckBlockNodeProps> = ({ data }) => {
  const [items, setItems] = useState(
    (data.checks || []).map((check, index) => ({
      id: index + 1,
      description: check.description,
      days: check.days,
    }))
  );

  const [description, setDescription] = useState<string>(data.description || "");

  const handleAdd = () => {
    setItems([...items, { id: items.length + 1, description: "", days: "" }]);
    updateChecks();
  };

  const handleDelete = (id: number) => {
    setItems(items.filter(item => item.id !== id));
    updateChecks();
  };

  const handleTextChange = (id: number, value: string) => {
    setItems(items.map(item => item.id === id ? { ...item, description: value } : item));
    updateChecks();
  };

  const handleDaysChange = (id: number, value: string) => {
    const numericValue = Math.min(999, Math.max(0, Number(value))).toString();
    setItems(items.map(item => item.id === id ? { ...item, days: numericValue } : item));
    updateChecks();
  };

  const handleDescriptionChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setDescription(event.target.value);
    data.description = event.target.value;
  };

  const updateChecks = () => {
    data.checks = items.map(item => ({
      description: item.description,
      days: item.days
    }));
  };

  return (
    <div className="bg-gray-500 text-white p-1 rounded shadow-md">
      <div className="flex items-center mb-2">
        <input
          type="text"
          value={description}
          onChange={handleDescriptionChange}
          placeholder="Задачи"
          className="flex-grow border rounded px-2 py-1 focus:outline-none text-black text-xs"
        />
      </div>

      <Handle type="target" position={Position.Top} />
      <Handle type="source" position={Position.Bottom} />

      <div className="w-72 shadow-md mt-2">
        <div className="bg-white p-2 space-y-2 rounded-lg">
          {items.map((item) => (
            <div key={item.id} className="flex items-center space-x-2">
              <input
                type="text"
                value={item.description}
                onChange={(e) => handleTextChange(item.id, e.target.value)}
                placeholder="Задача"
                className="flex-grow border rounded px-2 py-1 focus:outline-none text-black text-xs"
              />
              <input
                type="number"
                value={item.days}
                onChange={(e) => handleDaysChange(item.id, e.target.value)}
                placeholder="Дни"
                className="w-10 border rounded px-2 py-1 focus:outline-none text-black text-xs no-spinners"
                min={0}
                max={999}
              />
              <button onClick={() => handleDelete(item.id)} className="text-red-500">
                <TiDeleteOutline size={20} />
              </button>
            </div>
          ))}
          <button
            onClick={handleAdd}
            className="w-full bg-gray-200 text-gray-700 rounded py-1 mt-2 font-semibold"
          >
            +
          </button>
        </div>
      </div>
    </div>
  );
};

export default CheckBlockNode;
