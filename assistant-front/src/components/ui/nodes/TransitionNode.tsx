import { Handle, Position } from "@xyflow/react";

const TransitionNode = () => {
  return (
    <div className="bg-zinc-500 text-white p-2 rounded shadow-md">
      <strong>TransitionNode</strong>
      <Handle type="target" position={Position.Top} />
    </div>
  );
};

export default TransitionNode;
