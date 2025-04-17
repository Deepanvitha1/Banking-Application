import { NextResponse } from "next/server";

export async function POST() {
  try {
    const response = NextResponse.json({ message: "Logged out" });
    response.cookies.set("token", "", { expires: new Date(0) });
    response.cookies.set("userId", "", { expires: new Date(0) });
    return response;
  } catch (error) {
    return NextResponse.json(
      { error: "Logout failed" + error },
      { status: 500 }
    );
  }
}
