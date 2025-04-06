import { Handle, Position } from "@xyflow/react";

const PositiveEndNode = () => {
  return (
    <div className="bg-green-500 text-white p-2 rounded shadow-md">
      <strong>PositiveEnd</strong>
      <Handle type="target" position={Position.Top} />
    </div>
  );
};

export default PositiveEndNode;
