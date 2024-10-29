import { getSession } from "next-auth/react"


export async function fetchSessionData() {
  const session = await getSession();

  if (session) {
      console.log("Access Token:", session.accessToken);
      console.log("Refresh Token:", session.refreshToken);
  } else {
      console.log("No active session found");
  }
}