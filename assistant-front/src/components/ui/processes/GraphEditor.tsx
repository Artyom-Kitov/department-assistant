import React, { useState, useCallback, useEffect } from "react";
import {
  ReactFlow,
  Background,
  useNodesState,
  useEdgesState,
  addEdge,
  Controls,
  MiniMap,
} from "@xyflow/react";
import StartNode from "../nodes/StartNode";
import NegativeEndNode from "../nodes/NegativeEndNode";
import PositiveEndNode from "../nodes/PositiveEndNode";
import PredicateNode from "../nodes/PredicateNode";
import BlockNode from "../nodes/BlockNode";
import CheckBlockNode from "../nodes/CheckBlockNode";
import TransitionNode from "../nodes/TransitionNode";
import { FiPlus } from "react-icons/fi";
import { FaArrowRight } from "react-icons/fa";
import "@xyflow/react/dist/style.css";

const initialNodes = [
  {
    id: "0",
    type: "Start",
    position: { x: 0, y: 0 },
  },
];

const nodeTypes = {
  Start: StartNode,
  Block: BlockNode,
  "Check Block": CheckBlockNode,
  Predicate: PredicateNode,
  "Negative End": NegativeEndNode,
  "Positive End": PositiveEndNode,
  Transition: TransitionNode,
};

interface Step {
  id: number;
  type: number;
  duration: number;
  metaInfo?: { x: number; y: number };
  description?: string;
  data?: {
    next?: number;
    ifTrue?: number;
    ifFalse?: number;
    subtasks?: { description: string; duration: number; checked: boolean }[];
    isSuccessful?: boolean;
    processId?: string;
  };
}

interface ProcessCode {
  id?: string;
  name?: string;
  duration?: number;
  steps: Step[];
}

export default function GraphEditor({ code, setCode, processName }) {
  const [nodes, setNodes, onNodesChange] = useNodesState(initialNodes);
  const [edges, setEdges, onEdgesChange] = useEdgesState([]);
  const [menuOpen, setMenuOpen] = useState(false);
  const [parsedCode, setParsedCode] = useState<ProcessCode | null>(null);
  const [skipGraphToCode, setSkipGraphToCode] = useState(false);

  const onConnect = useCallback(
    (params) => setEdges(addEdge(params, edges)),
    [edges]
  );

  const addNode = (type: string) => {
    const lastNode = nodes[nodes.length - 1];
    const newId = (parseInt(lastNode.id) + 1).toString();
    const newPosition = {
      x: lastNode.position.x,
      y: lastNode.position.y + 100,
    };

    const newNode = {
      id: newId,
      type,
      position: newPosition,
      data: {},
    };

    setNodes((prev) => [...prev, newNode]);
    setMenuOpen(false);
  };

  const convertGraphToCode = () => {
    console.log("Convert graph to code");
    setSkipGraphToCode(true); 
    const steps = nodes.map((node) => {
        const baseStep = {
            id: Number(node.id),
            metaInfo: { x: node.position.x, y: node.position.y },
        };

        switch (node.type) {
            case "Start":
                return {
                    ...baseStep,
                    type: 0,
                    duration: 0,
                    description: "Start",
                    data: {
                        next: getNextStepId(node.id),
                    },
                };
            case "Predicate":
                return {
                    ...baseStep,
                    type: 3,
                    duration: node.data.days,
                    description: node.data.description || "Без описания",
                    data: {
                        ifTrue: node.data.trueNodeId || node.data.ifTrue,
                        ifFalse: node.data.falseNodeId || node.data.ifFalse,
                    },
                };
            case "Check Block":
                return {
                    ...baseStep,
                    type: 2,
                    duration: node.data.days,
                    description: node.data.description || "Без описания",
                    data: {
                        subtasks: node.data.checks
                            ? node.data.checks.map((item) => ({
                                description: item.description,
                                duration: item.days || 1,
                            }))
                            : [],
                        next: getNextStepId(node.id),
                    },
                };
            case "Block":
                return {
                    ...baseStep,
                    type: 1,
                    duration: node.data.days,
                    description: node.data.description || "Без описания",
                    data: {
                        next: getNextStepId(node.id),
                    },
                };
            case "Negative End":
            case "Positive End":
                return {
                    ...baseStep,
                    type: 4,
                    duration: 0,
                    description: "Без описания",
                    data: {
                        isSuccessful: node.type === "Positive End",
                    },
                };
            case "Transition":
                return {
                    ...baseStep,
                    type: 5,
                    data: {
                        processId: node.data.processId || "default-process-id",
                    },
                };
            default:
                return null;
        }
    });

    const code = {
        name: processName || "",
        steps,
    };

    setCode(JSON.stringify(code, null, 2));
    setParsedCode(JSON.parse(JSON.stringify(code, null, 2)));
};

  useEffect(() => {
    if (skipGraphToCode) {
        setSkipGraphToCode(false); // Сбрасываем флаг после первого вызова
        return; // Пропускаем выполнение convertCodeToGraph
    }

    try {
        console.log(code);
        if (!code) return;
        setParsedCode(JSON.parse(code));
        convertCodeToGraph();
    } catch (error) {
        console.error("Invalid JSON format:", error);
    }
}, [code]);

  const convertCodeToGraph = () => {
    console.log("Convert code to graph");
    console.log(parsedCode);
    if (!parsedCode || !parsedCode.steps) return;

    const newNodes = parsedCode.steps.map((step) => ({
        id: step.id.toString(),
        type: getNodeType(step),
        position: step.metaInfo || { x: 0, y: 0 },
        data: {
            ...step.data,
            description: step.description || "Без описания",
            days: step.duration || 1,
            checks: step.data?.subtasks
                ? step.data.subtasks.map((task) => ({
                    description: task.description,
                    days: task.duration.toString(),
                }))
                : [],
        },
    }));

    const newEdges = parsedCode.steps.flatMap((step) => {
        if (!step.data) return [];

        const edges = [];

        // Обычное соединение для next
        if (step.data.next) {
            edges.push({
                id: `e${step.id}-${step.data.next}`,
                source: step.id.toString(),
                target: step.data.next.toString(),
            });
        }

        // Проверка, является ли шаг предикатным блоком
        if (getNodeType(step) === "Predicate") {
            if (step.data.ifTrue) {
                edges.push({
                    id: `e${step.id}-true-${step.data.ifTrue}`,
                    source: step.id.toString(),
                    sourceHandle: "source-left",
                    target: step.data.ifTrue.toString(),
                });
            }
            if (step.data.ifFalse) {
                edges.push({
                    id: `e${step.id}-false-${step.data.ifFalse}`,
                    source: step.id.toString(),
                    sourceHandle: "source-right",
                    target: step.data.ifFalse.toString(),
                });
            }
        }

        return edges;
    });

    setNodes(newNodes);
    setEdges(newEdges);
};

  

  const getNodeType = (step) => {
    switch (step.type) {
      case 0:
        return "Start";
      case 1:
        return "Block";
      case 2:
        return "Check Block";
      case 3:
        return "Predicate";
      case 4:
        if (!step.data.isSuccessful) return "Negative End";
        return "Positive End"; 
      case 5:
        return "Transition";
      default:
        return "Block";
    }
  };

  const getNextStepId = (currentId, branch = "") => {
    const connectedEdges = edges.filter((edge) => edge.source === currentId);
    const nextNode = connectedEdges.find((edge) =>
      branch ? edge.type === branch : true
    );
    return Number(nextNode ? nextNode.target : null);
  };

  return (
    <div className="relative w-full h-screen">
      <div className="absolute top-0 right-0 z-10 bg-white border rounded-bl-xl shadow-md p-4">
        <div className="flex flex-col items-center space-y-2">
          <button
            onClick={convertGraphToCode}
            className="flex items-center gap-2 bg-blue-500 text-white text-sm px-4 py-2 rounded w-full hover:bg-blue-600"
          >
            Graph <FaArrowRight /> Code
          </button>
          <button 
            onClick={convertCodeToGraph}
            className="flex items-center gap-2 bg-blue-500 text-white text-sm  px-4 py-2 rounded w-full hover:bg-blue-600">
            Code <FaArrowRight /> Graph
          </button>
          <div className="mt-4">
            <button
              className="bg-green-500 text-white p-4 rounded-full shadow-lg"
              onClick={() => setMenuOpen((prev) => !prev)}
            >
              <FiPlus />
            </button>
          </div>
        </div>
        {menuOpen && (
          <div className="absolute right-0 mt-2 w-40 bg-white border rounded shadow-md">
            {Object.keys(nodeTypes).map((key) => (
              <button
                key={key}
                className="block w-full text-left px-4 py-2 hover:bg-gray-200"
                onClick={() => addNode(key)}
              >
                {key}
              </button>
            ))}
          </div>
        )}
      </div>

      <ReactFlow
        nodes={nodes}
        edges={edges}
        onNodesChange={onNodesChange}
        onEdgesChange={onEdgesChange}
        onConnect={onConnect}
        fitView
        attributionPosition="top-right"
        nodeTypes={nodeTypes}
        style={{ backgroundColor: "#F7F9FB" }}
      >
        <Background />
        <Controls />
        <MiniMap />
      </ReactFlow>
    </div>
  );
}
