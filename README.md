# Repo dedicated to various Tensorflow-metal experiments

## How to run on M1 chip with GPU support

Install conda:
```
chmod +x ~/Downloads/Miniforge3-MacOSX-arm64.sh
sh ~/Downloads/Miniforge3-MacOSX-arm64.sh
```

Create conda environment:
```
source ~/miniforge3/bin/activate
conda create -n tensorflow python=3.9.5
conda activate tensorflow
```

Install dependencies:
```
conda install -c apple tensorflow-deps
python -m pip install tensorflow-macos
python -m pip install tensorflow-metal
brew install libjpeg
conda install -y matplotlib jupyterlab
```

Test installation by running Jupyter lab
```
jupyter lab
```
Run `tensorflow_test.ipynb`, check Activity monitor to check that GPU is utilised
