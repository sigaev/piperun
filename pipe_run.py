import os
import subprocess
import sys


def main():
	p = subprocess.Popen(sys.argv[1:],
						 stdout=subprocess.PIPE,
						 stderr=subprocess.STDOUT,
						 env=os.environ.copy(),
						 shell=True)
	for line in p.stdout:
		print(line.decode("utf-8").rstrip())
	p.wait()
	return p.returncode


if __name__ == "__main__":
	sys.exit(main())
