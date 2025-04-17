import { NextResponse } from "next/server";
import axios from "axios";
import { cookies } from "next/headers";
import config from "@/next.config";
import { endpoints } from "@/app/auth/_apis/endpoints";

export const GET = async () => {
  try {
    const cookieStore = cookies();
    const token = (await cookieStore).get("token")?.value;
    const userId = (await cookieStore).get("userId")?.value;

    if (!token || !userId) {
      return NextResponse.json(
        { success: false, error: "Unauthorized" },
        { status: 401 }
      );
    }
    const response = await axios.get(
      `${config.api.endpoints}${endpoints.getUserDetails.replace(
        "{userId}",
        userId
      )}`,
      {
        headers: { Authorization: `Bearer ${token}` },
      }
    );

    return NextResponse.json({ success: true, data: response.data });
  } catch (error) {
    return NextResponse.json(
      {
        success: false,
        error: "Failed to fetch user details: " + error,
      },
      { status: 500 }
    );
  }
};
