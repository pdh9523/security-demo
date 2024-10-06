"use client";

import Image from "next/image";
import { useRouter } from "next/navigation";
import { grey } from "@mui/material/colors";
import {ChangeEvent, useState} from "react";
import {getSession, signIn} from "next-auth/react";
import {
    Box,
    Container,
    Grid,
    TextField,
    Typography,
    Button,
    Divider,
} from "@mui/material";

interface HandleLoginProps {
    email: string;
    password: string;
}

export function handleValueChange(
    event: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
    setFunction: (value: string) => void,
) {
    setFunction(event.target.value);
}

function goSocialLogin(provider: string) {
    // NextAuth의 signIn 함수를 사용하여 소셜 로그인 처리
    signIn(provider);
}

export async function fetchSessionData() {
    const session = await getSession();

    if (session) {
        console.log("Access Token:", session.accessToken);
        console.log("Refresh Token:", session.refreshToken);
    } else {
        console.log("No active session found");
    }
}
export default function App() {
    const router = useRouter();

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");

    async function handleLogin({ email, password }: HandleLoginProps) {
        const result = await signIn("credentials", {
            redirect: false,
            email,
            password,
        });

        if (result?.error) {
            // 로그인 실패 시 에러 메시지 표시
            setError("로그인에 실패했습니다. 이메일과 비밀번호를 확인해주세요.");
        } else {
            // 로그인 성공 시 메인 페이지로 이동
            router.push("/");
        }
    }

    const response = getSession();
    console.log(response)
    fetchSessionData()
    return (
        <Container component="main" maxWidth="xs">
            <Box
                sx={{
                    marginTop: 8,
                    display: "flex",
                    flexDirection: "column",
                    alignItems: "center",
                }}
            >
                <Typography component="h1" variant="h5">
                    로그인
                </Typography>
                <Box
                    component="form"
                    onSubmit={(event) => {
                        event.preventDefault();
                        handleLogin({ email, password });
                    }}
                    noValidate
                    sx={{ mt: 1 }}
                >
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        id="email"
                        label="이메일"
                        autoComplete="email"
                        value={email}
                        onChange={(event) => handleValueChange(event, setEmail)}
                        autoFocus
                    />
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        id="password"
                        label="비밀번호"
                        type="password"
                        value={password}
                        onChange={(event) => handleValueChange(event, setPassword)}
                        autoComplete="current-password"
                    />
                    {error && (
                        <Typography color="error" variant="body2">
                            {error}
                        </Typography>
                    )}
                    <Button
                        type="submit"
                        fullWidth
                        variant="contained"
                        sx={{
                            mt: 3,
                            mb: 2,
                            height: "56px",
                        }}
                    >
                        로그인
                    </Button>
                    <Grid
                        container
                        sx={{
                            display: "flex",
                            justifyContent: "space-between",
                        }}
                    ></Grid>
                    <Divider
                        sx={{
                            mt: 2,
                            color: grey[500],
                        }}
                    >
                        또는
                    </Divider>
                    <Grid container spacing={1} mt={2}>
                        <Grid item xs={6}>
                            <Box
                                sx={{
                                    width: "100%",
                                    height: "56px",
                                    cursor: "pointer",
                                    position: "relative",
                                }}
                                onClick={() => goSocialLogin("naver")}
                            >
                                <Image
                                    src="/naverBtn.png"
                                    alt="naverLogin"
                                    layout="fill" // 부모의 크기에 맞게 조절
                                    objectFit="contain"
                                />
                            </Box>
                        </Grid>
                        <Grid item xs={6}>
                            <Box
                                sx={{
                                    width: "100%",
                                    height: "56px",
                                    cursor: "pointer",
                                    position: "relative",
                                }}
                                onClick={() => goSocialLogin("kakao")}
                            >
                                <Image
                                    src="/kakao_login_medium_narrow.png"
                                    alt="kakaoLogin"
                                    layout="fill" // 부모의 크기에 맞게 조절
                                    objectFit="contain"
                                />
                            </Box>
                        </Grid>
                    </Grid>
                </Box>
            </Box>
        </Container>
    );
}
