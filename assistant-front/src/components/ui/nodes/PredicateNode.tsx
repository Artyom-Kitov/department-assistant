import { Handle, Position } from "@xyflow/react";
import { useState } from "react";
import { FiPlus, FiMinus } from "react-icons/fi";

interface PredicateNodeProps {
  data: {
    description: string;
    days: string;
    trueNodeId: string;
    falseNodeId: string;
  };
}

const PredicateNode: React.FC<PredicateNodeProps> = ({ data }) => {
  const [text, setText] = useState<string>(data.description || "");
  const [days, setDays] = useState<string>(data.days || "");

  const handleTextChange = (event: React.ChangeEvent<HTMLTextAreaElement>) => {
    setText(event.target.value);
    data.description = event.target.value;
  };

  const handleDaysChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const value = Math.min(999, Math.max(0, Number(event.target.value))).toString();
    setDays(value);
    data.days = value;
  };

  const handleConnect = (connection: { source: string; target: string }) => {

    const { source, target } = connection;
    if (source === "source-left") {
      data.trueNodeId = target;
    } else if (source === "source-right") {
      data.falseNodeId = target;
    }
  };

  return (
    <div className="bg-yellow-500 text-white p-1 rounded shadow-md">
      <div className="flex items-center space-x-2">
        <FiPlus size={20} />

        <div className="flex flex-col items-end">
          <input
            type="number"
            value={days}
            onChange={handleDaysChange}
            placeholder="Дни"
            className="w-10 border rounded px-2 py-1 focus:outline-none text-black text-xs no-spinners"
            min={0}
            max={999}
          />

          <textarea
            value={text}
            onChange={handleTextChange}
            placeholder="Условие"
            className="w-full mt-2 p-2 rounded text-black text-xs"
            rows={4}
            style={{ resize: "none" }}
          />
        </div>

        <FiMinus size={20} />
      </div>

      <Handle
        type="target"
        position={Position.Top}
        id="target-top"
      />
      <Handle
        type="source"
        position={Position.Left}
        id="source-left"
        onConnect={(params) => handleConnect({ source: "source-left", target: params.target })}
      />
      <Handle
        type="source"
        position={Position.Right}
        id="source-right"
        onConnect={(params) => handleConnect({ source: "source-right", target: params.target })}
      />
    </div>
  );
};

export default PredicateNode;
