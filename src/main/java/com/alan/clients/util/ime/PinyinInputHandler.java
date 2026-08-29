package com.alan.clients.util.ime;

import com.alan.clients.util.gui.textbox.TextBox;
import com.alan.clients.util.ime.PinyinImeState;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class PinyinInputHandler {
    private final PinyinDictionary aJR = PinyinDictionary.um();
    private static final int aJS = 9;
    private static final int aJT = 64;
    private boolean aJU;
    private String aJV = "";
    private List<String> aJW = Collections.emptyList();
    private List<String> aJX = Collections.emptyList();
    private int aJY = 0;
    private int aAP = 0;
    private int aJZ = PinyinImeState.uA();

    public PinyinInputHandler() {
    }

    public int un() {
        this.uu();
        return this.aAP;
    }

    public boolean uc() {
        this.uu();
        return this.aJU;
    }

    public String uo() {
        this.uu();
        return this.aJV;
    }

    public List<String> up() {
        this.uu();
        return this.aJW;
    }

    public int uq() {
        this.uu();
        return this.aJX == null ? 0 : this.aJX.size();
    }

    public int ur() {
        this.uu();
        if (!this.aJU) {
            return 0;
        }
        return this.aJY <= 0 ? 0 : this.aJY / 9;
    }

    public int us() {
        this.uu();
        int i = this.uq();
        return i <= 0 ? 0 : (i + 9 - 1) / 9;
    }

    public String ut() {
        this.uu();
        if (this.uq() <= 9) {
            return "";
        }

        int i = this.ur() + 1;
        int j = this.us();
        return " (" + i + "/" + j + ")";
    }

    public void aX() {
        this.aJU = false;
        this.aJV = "";
        this.aJW = Collections.emptyList();
        this.aJX = Collections.emptyList();
        this.aJY = 0;
        this.aAP = 0;
    }

    private void uu() {
        int i = PinyinImeState.uA();
        if (i != this.aJZ) {
            this.aX();
            this.aJZ = i;
        }
    }

    public boolean a(TextBox textBox, char var2, int var3) {
        if (textBox == null) {
            return false;
        }

        this.uu();
        if (this.aJU) {
            if ((var3 == 201 || var3 == 26) && this.uy()) {
                return true;
            }

            if ((var3 == 209 || var3 == 27) && this.uz()) {
                return true;
            }
        }

        if (this.aJU) {
            if (var3 == 15) {
                if (this.aJW != null && !this.aJW.isEmpty()) {
                    this.aAP = (this.aAP + 1) % this.aJW.size();
                }

                return true;
            }

            if (var3 == 200) {
                if (this.uq() > 9) {
                    this.uy();
                    return true;
                }

                if (this.aJW != null && !this.aJW.isEmpty()) {
                    this.aAP = (this.aAP - 1 + this.aJW.size()) % this.aJW.size();
                }

                return true;
            }

            if (var3 == 208) {
                if (this.uq() > 9) {
                    this.uz();
                    return true;
                }

                if (this.aJW != null && !this.aJW.isEmpty()) {
                    this.aAP = (this.aAP + 1) % this.aJW.size();
                }

                return true;
            }

            if (var3 == 203) {
                if (this.aJW != null && !this.aJW.isEmpty()) {
                    this.aAP = (this.aAP - 1 + this.aJW.size()) % this.aJW.size();
                }

                return true;
            }

            if (var3 == 205) {
                if (this.aJW != null && !this.aJW.isEmpty()) {
                    this.aAP = (this.aAP + 1) % this.aJW.size();
                }

                return true;
            }
        }

        if (var3 == 1) {
            if (this.aJU) {
                this.aX();
                return true;
            }
            return false;
        } else if (this.aJU && var3 >= 2 && var3 <= 10) {
            int i = var3 - 2;
            if (i < this.aJW.size()) {
                this.a(textBox, this.aJY + i);
            }

            return true;
        } else if (var3 == 14) {
            if (!this.aJU) {
                return false;
            }

            if (!this.aJV.isEmpty()) {
                this.aJV = this.aJV.substring(0, this.aJV.length() - 1);
                if (this.aJV.isEmpty()) {
                    this.aX();
                } else {
                    this.uw();
                }
            } else {
                this.aX();
            }

            return true;
        } else if (var3 != 57 && var3 != 28) {
            if (n(var2)) {
                char c0 = Character.toLowerCase(var2);
                this.uv();
                this.aJV = this.aJV + c0;
                this.uw();
                return true;
            }

            if (this.aJU && var2 != 0 && !Character.isISOControl(var2)) {
                if (!this.aJW.isEmpty()) {
                    this.a(textBox, this.aJY + this.aAP);
                } else if (!this.aJV.isEmpty()) {
                    textBox.bV(this.aJV);
                    this.aX();
                }

                return false;
            }
            return false;
        } else {
            if (!this.aJU) {
                return false;
            }

            if (!this.aJW.isEmpty()) {
                this.a(textBox, this.aJY + this.aAP);
            } else {
                textBox.bV(this.aJV);
                this.aX();
            }

            return true;
        }
    }

    private void uv() {
        if (!this.aJU) {
            this.aJU = true;
            this.aJV = "";
            this.aJW = Collections.emptyList();
            this.aJX = Collections.emptyList();
            this.aJY = 0;
            this.aAP = 0;
        }
    }

    private void uw() {
        if (this.aJU) {
            String s = this.aJV.toLowerCase(Locale.ROOT);
            ArrayList arraylist = new ArrayList<>(this.aJR.s(s, 64));
            this.aJX = arraylist;
            this.aJY = 0;
            this.aJW = this.ux();
            this.aAP = 0;
        }
    }

    private List<String> ux() {
        if (this.aJX != null && !this.aJX.isEmpty()) {
            int i = Math.max(0, Math.min(this.aJY, this.aJX.size() - 1));
            int j = Math.min(this.aJX.size(), i + 9);
            return i >= j ? Collections.emptyList() : this.aJX.subList(i, j);
        }
        return Collections.emptyList();
    }

    private boolean uy() {
        if (!this.aJU) {
            return false;
        }

        if (this.uq() <= 9) {
            return false;
        }

        int i = Math.max(0, this.aJY - 9);
        if (i == this.aJY) {
            return false;
        }

        this.aJY = i;
        this.aJW = this.ux();
        this.aAP = Math.min(this.aAP, Math.max(0, this.aJW.size() - 1));
        return true;
    }

    private boolean uz() {
        if (!this.aJU) {
            return false;
        }

        int i = this.uq();
        if (i <= 9) {
            return false;
        }

        int j = Math.min(Math.max(0, (i - 1) / 9 * 9), this.aJY + 9);
        if (j == this.aJY) {
            return false;
        }

        this.aJY = j;
        this.aJW = this.ux();
        this.aAP = Math.min(this.aAP, Math.max(0, this.aJW.size() - 1));
        return true;
    }

    private void a(TextBox textBox, int var2) {
        if (this.aJU) {
            if (this.aJX != null && !this.aJX.isEmpty()) {
                int i = Math.max(0, Math.min(var2, this.aJX.size() - 1));
                String s = this.aJX.get(i);
                String s1 = this.aJV == null ? "" : this.aJV.toLowerCase(Locale.ROOT);
                textBox.bV(s);
                if (!s1.isEmpty() && s != null && !s.isEmpty()) {
                    PinyinUsageStore.uC().D(s1, s);
                }

                this.aX();
            }
        }
    }

    private static boolean n(char var0) {
        return var0 >= 'a' && var0 <= 'z' || var0 >= 'A' && var0 <= 'Z';
    }
}
