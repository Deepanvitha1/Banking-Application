import Sidebar from "../../components/sidebar-component/Sidebar";
export default function Layout({
  children,
}: Readonly<{ children: React.ReactNode }>) {
  return (
    <div style={{ display: "flex" }}>
      <Sidebar />
      <main style={{ marginLeft: "250px", padding: "20px", flexGrow: 1 }}>
        {children}
      </main>
    </div>
  );
}
