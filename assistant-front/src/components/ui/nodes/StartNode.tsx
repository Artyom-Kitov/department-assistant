import { Handle, Position } from "@xyflow/react";

const StartNode = () => {
  return (
    <div className="bg-green-500 text-white p-2 rounded shadow-md">
      <strong>Старт</strong>
      <Handle type="source" position={Position.Bottom} />
    </div>
  );
};

export default StartNode;
