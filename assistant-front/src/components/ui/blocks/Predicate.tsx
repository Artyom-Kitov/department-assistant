const Predicate: React.FC = () => (
  <div className="relative w-32 h-32 transform rotate-45 bg-gray-300 shadow-md">
    <input
      type="text"
      placeholder="???"
      className="absolute inset-0   flex items-center justify-center bg-white border border-gray-300 text-center focus:outline-none transform rotate-[-45deg]"
    />
  </div>
);

export default Predicate;
