#include <Windows.h>

#define MACROBOARD_API extern "C" __declspec(dllexport)

void typeKey(WORD key)
{
	KEYBDINPUT ki = KEYBDINPUT();
	ki.wVk = key;

	INPUT i = INPUT();
	i.type = INPUT_KEYBOARD;
	i.ki = ki;

	INPUT i2 = INPUT(i);
	i.ki.dwFlags |= KEYEVENTF_KEYUP;

	INPUT a[] = { i, i2 };

	SendInput(2, a, sizeof(i));
}

MACROBOARD_API void typeMuteKey()
{
	typeKey(VK_VOLUME_MUTE);
}

MACROBOARD_API void typeVolumeKey(bool volumeUp)
{
	typeKey(volumeUp ? VK_VOLUME_UP : VK_VOLUME_DOWN);
}

MACROBOARD_API void typeUnicode(WORD unicode)
{
	KEYBDINPUT ki = KEYBDINPUT();
	ki.wVk = 0;
	ki.dwFlags = KEYEVENTF_UNICODE;
	ki.wScan = unicode;

	INPUT i = INPUT();
	i.type = INPUT_KEYBOARD;
	i.ki = ki;

	INPUT i2 = INPUT(i);
	i2.ki.dwFlags |= KEYEVENTF_KEYUP;

	INPUT array[] = { i, i2 };

	SendInput(2, array, sizeof(i));
}
