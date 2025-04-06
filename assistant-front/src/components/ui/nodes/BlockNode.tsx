import { Handle, Position } from "@xyflow/react";
import { useState } from "react";
import "./no-spinners.css";

// Определяем тип для пропсов
interface BlockNodeProps {
  data: {
    description: string; 
    days: string;
  };
}

const BlockNode: React.FC<BlockNodeProps> = ({ data }) => {
  const [description, setDescription] = useState<string>(data.description || "");  
  const [days, setDays] = useState<string>(data.days || "");  

  const handleDescriptionChange = (event: React.ChangeEvent<HTMLTextAreaElement>) => {
    setDescription(event.target.value);
    data.description = event.target.value;  
  };

  const handleDaysChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const value = Math.min(999, Math.max(0, Number(event.target.value))).toString();
    setDays(value);
    data.days = value; 
  };

  return (
    <div className="bg-gray-500 text-white p-1 rounded shadow-md flex flex-col items-end">
      <input
        type="number"
        value={days}
        onChange={handleDaysChange}
        placeholder="Дни"
        className="w-10 border rounded px-2 py-1 focus:outline-none text-black text-xs no-spinners "
        min={0}
        max={999}
      />

      <textarea
        value={description}
        onChange={handleDescriptionChange}
        placeholder="Задача"
        className="w-full mt-2 p-2 rounded text-black text-xs"
        rows={4}
        style={{ resize: "none" }}
      />

      <Handle type="target" position={Position.Top} />
      <Handle type="source" position={Position.Bottom} />
    </div>
  );
};

export default BlockNode;
