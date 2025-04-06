import { Handle, Position } from "@xyflow/react";

const NegativeEndNode = () => {
  return (
    <div className="bg-red-500 text-white p-2 rounded shadow-md">
      <strong>NegativeEnd</strong>
      <Handle type="target" position={Position.Top} />
    </div>
  );
};

export default NegativeEndNode;
