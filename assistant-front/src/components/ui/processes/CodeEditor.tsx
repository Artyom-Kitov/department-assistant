import React, { useRef } from "react";

const CodeEditor: React.FC<{
  code: string;
  setCode: React.Dispatch<React.SetStateAction<string>>;
}> = ({ code, setCode }) => {
  const textareaRef = useRef<HTMLTextAreaElement>(null);
  const lineNumbersRef = useRef<HTMLDivElement>(null);

  // Handle code change (input event)
  const handleChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    setCode(e.target.value);
  };

  // Handle keydown events (Tab, Enter, etc.)
  const handleKeyDown = (e: React.KeyboardEvent<HTMLTextAreaElement>) => {
    if (!textareaRef.current) return;

    const { selectionStart, selectionEnd, value } = textareaRef.current;

    // Handle Tab key (indentation)
    if (e.key === "Tab") {
      e.preventDefault();
      const newValue =
        value.substring(0, selectionStart) +
        "    " +
        value.substring(selectionEnd);
      setCode(newValue);
      setTimeout(() => {
        textareaRef.current!.selectionStart =
          textareaRef.current!.selectionEnd = selectionStart + 4;
      }, 0);
    }

    // Handle auto-closing brackets
    const brackets: Record<string, string> = {
      "(": ")",
      "{": "}",
      "[": "]",
      '"': '"',
      "'": "'",
    };

    if (brackets[e.key]) {
      e.preventDefault();
      const closeBracket = brackets[e.key];
      const newValue =
        value.substring(0, selectionStart) +
        e.key +
        closeBracket +
        value.substring(selectionEnd);
      setCode(newValue);
      setTimeout(() => {
        textareaRef.current!.selectionStart =
          textareaRef.current!.selectionEnd = selectionStart + 1;
      }, 0);
    }

    // Handle auto-indent on Enter key
    if (e.key === "Enter") {
      e.preventDefault();

      // Get the line before the cursor
      const lines = value.substring(0, selectionStart).split("\n");
      const prevLine = lines[lines.length - 1];

      // Determine indentation (spaces/tabs)
      const match = prevLine.match(/^\s*/);
      const indent = match ? match[0] : "";

      // Insert a new line with the same indentation
      const newValue =
        value.substring(0, selectionStart) +
        "\n" +
        indent +
        value.substring(selectionEnd);
      setCode(newValue);

      setTimeout(() => {
        textareaRef.current!.selectionStart =
          textareaRef.current!.selectionEnd =
            selectionStart + 1 + indent.length;
      }, 0);
    }
  };

  // Sync the scroll position of line numbers with the textarea
  const handleScroll = () => {
    if (textareaRef.current && lineNumbersRef.current) {
      lineNumbersRef.current.scrollTop = textareaRef.current.scrollTop;
    }
  };

  return (
    <div className="relative w-full h-screen bg-white flex border border-gray-300 rounded-lg shadow-md">
      {/* Line Numbers Section */}
      <div
        ref={lineNumbersRef}
        className="w-12 bg-gray-200 text-gray-600 text-sm flex flex-col items-end py-2 pr-3 overflow-hidden border-r border-gray-300"
        style={{ lineHeight: "1.5rem" }}
      >
        {(code ?? "").split("\n").map((_, i) => (
          <div key={i}>{i + 1}</div>
        ))}
      </div>

      {/* Textarea Section */}
      <div className="flex-1 overflow-hidden">
        <textarea
          ref={textareaRef}
          className="w-full h-full text-gray-900 bg-transparent outline-none px-4 pt-2 text-sm font-mono resize-none overflow-auto focus:ring-2 focus:ring-blue-500"
          value={code}
          onChange={handleChange}
          onKeyDown={handleKeyDown}
          onScroll={handleScroll}
          spellCheck={false}
          style={{ lineHeight: "1.5rem" }}
        />
      </div>
    </div>
  );
};

export default CodeEditor;
